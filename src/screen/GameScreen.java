package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Queue;
import java.util.LinkedList;
import engine.*;
import entity.*;
import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.GameState;
import entity.Bullet;
import entity.BulletPool;
import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Entity;
import entity.Ship;
import entity.Shield;
import sound.*;

/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 20000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 10000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;
	/** Milliseconds during the screen display the item info. */
	private static final int ITEM_DISPLAY_TIME = 2000;

	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());

	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Player's ship. */
	private Ship ship;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;

	private ExpManager expManager;
	/** Dangerous enemy ship that appears sometimes. */
	private EnemyShip enemyShipDangerous;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Minimum time between dangerous ship appearances. */
	private Cooldown enemyShipDangerousCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time until dangerous ship explosion disappears. */
	private Cooldown enemyShipDangerousExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** */
	private Cooldown itemInfoCooldown;
	/** Set of all bullets fired by on-screen ships. */
	private Set<Bullet> bullets;

	private Queue<EXPItem> expItems;

	private int EXP_SPEED = 2;
	/** Current score. */
	private int score;
	/** Player lives left. */
	public static int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;

	private ItemManager itemmanager;

	private Item item;

	private ItemPool itemPool;

	private Set<Item> itemIterator;

	private Shield shield;

	private int past_countdown=4;

	private EnemyShipGenerator enemyShipGenerator;

	private Cooldown enemyGenerateCooldown;

	private static final int ENEMY_GENERATE_INTERVAL = 5000;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
			final GameSettings gameSettings,
			final int width, final int height, final int fps) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.expManager = new ExpManager();
		lives = gameState.getLivesRemaining();
		if(lives == 0){
			SoundPlay.getInstance().stopBgm();
		}
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();

		this.itemmanager = new ItemManager();

		if(this.itemPool == null){
			this.itemPool = new ItemPool();
		}

	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				SoundPlay.getInstance().play(SoundType.inGameBGM);
			}
		};
		Timer timer = new Timer("Timer");
		long delay = 5800L;
		timer.schedule(task, delay);

		int playerShipShape = FileManager.getPlayerShipShape();
		switch (playerShipShape) {
			case 0:
				this.ship = new Ship(this.width / 2, this.height - 30, FileManager.ChangeIntToColor());
				break;
			case 1:
				this.ship = new Ship(this.width / 2, this.height - 30, playerShipShape, FileManager.ChangeIntToColor());
				break;
			case 2:
				this.ship = new Ship(this.width / 2, this.height - 30, (char) ('0'+playerShipShape), FileManager.ChangeIntToColor());
				break;
		}
		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		//add dangerous Ship
		this.enemyShipDangerousCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipDangerousCooldown.reset();
		this.enemyShipDangerousExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		///////////////////////////////////
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<>();
		this.expItems = new LinkedList<>();
		this.itemIterator = new HashSet<>();
		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();

		this.itemInfoCooldown = Core.getCooldown(ITEM_DISPLAY_TIME);

		enemyShipGenerator = new EnemyShipGenerator(3);
		enemyShipGenerator.GenerateEnemyShip(gameSettings);
		enemyGenerateCooldown = Core.getCooldown(ENEMY_GENERATE_INTERVAL);
		for (int i = 0; i < 3; i++) {
			if(enemyShipGenerator.isAlive(i)){
				EnemyShipGenerator.shipFormationList.get(i).attach(this);
			}
		}
		enemyGenerateCooldown.reset();

	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		SoundPlay.getInstance().stopBgm();
		this.score += LIFE_SCORE * (lives - 1);
		this.logger.info("Screen cleared with a score of " + this.score); // 정상 출력
		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && !this.levelFinished) {

			if (!this.ship.isDestroyed()) operateShip();

			if (this.enemyShipSpecial != null) operateSpecialShip();
			else generateSpecialShip();

			if (this.enemyShipDangerous != null) operateDangerousShip();
			else generateDangerousShip();

			generateEnemyShip();
			this.ship.update();
			enemyShipGenerator.update();
			enemyShipGenerator.shoot(this.bullets);
		}
		for(Item item : this.itemIterator){
			if(item != null) {
				manageGetItem(item);
			}
		}
		manageCollisions();
		cleanItems();
		cleanBullets();
		cleanEXPItems();
		draw();
		if ( lives == 0 && !this.levelFinished) {
			this.levelFinished = true;
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
			this.isRunning = false;

		//수정 필요
//		if(this.enemyShipFormation.isEmpty()){
//			sound.SoundPlay.getInstance().play(SoundType.roundClear);
//			this.isRunning = false;
//		}


		// destroy EnemyShip when escape the screen
		for (EnemyShipFormation formation :
				EnemyShipGenerator.shipFormationList) {
			for (EnemyShip ship : formation) {
				if (ship.isDestroyed()) continue;
				if (!isEnemyInScreen(ship)) formation.destroy(ship);
			}
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);


		for(Item item : this.itemIterator) {
			if (item != null) {
				drawManager.drawEntity(item, item.getPositionX(),
						item.getPositionY());
			}
		}
		drawManager.drawEntity(this.ship, this.ship.getPositionX(),
				this.ship.getPositionY());
		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		if (this.enemyShipDangerous != null)
			drawManager.drawEntity(this.enemyShipDangerous,
					this.enemyShipDangerous.getPositionX(),
					this.enemyShipDangerous.getPositionY());
		if(shield != null){
				drawManager.drawEntity(shield, shield.getPositionX(),shield.getPositionY());}

		if(itemPool.getItem() != null && this.shield != null &&
				itemPool.getItem().getItemType() == Item.ItemType.ShieldItem){
				drawManager.drawEntity(shield, shield.getPositionX(),
				shield.getPositionY());}

		enemyShipGenerator.draw();

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());

		for(EXPItem expItem : this.expItems)
			drawManager.drawEntity(expItem, expItem.getPositionX(), expItem.getPositionY());


		// Interface.
		drawManager.drawLevels(this, this.level);
		drawManager.drawScore(this, this.score);
		drawManager.drawLives(this, lives);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY
					- (System.currentTimeMillis()
							- this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, countdown);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12);
			if ((past_countdown > countdown)) {
				ExecutorService executorService = Executors.newCachedThreadPool();
				executorService.submit(() -> {
					past_countdown = countdown;
					if(countdown==0) sound.SoundPlay.getInstance().play(SoundType.roundStart);
					if(countdown>0) sound.SoundPlay.getInstance().play(SoundType.roundCounting);
				});
				executorService.shutdown();
			}
		}

		if(!this.itemInfoCooldown.checkFinished()){
			drawManager.drawItemInfo(this, this.returnCode);
		}

		drawManager.completeDrawing(this);
	}

	/**
	 * Cleans bullets that go off-screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	private void cleanEXPItems(){
		Set<EXPItem> recyclable = new HashSet<>();
		int cnt = 0;
		for(EXPItem expItem : this.expItems){
			expItem.update();
			if(expItem.getPositionY() < SEPARATION_LINE_HEIGHT
					|| expItem.getPositionY() > this.height-35){
				expItem.setSpeed(0);
			}

		}
		if(this.expItems.size() > 10){
			recyclable.add(this.expItems.poll());
		}
		this.expItems.removeAll(recyclable);
		EXPItemPool.recycle(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<>();
		Set<EXPItem> expRecyclable = new HashSet<>();
		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished) {
					recyclable.add(bullet);


					if (shield == null && !this.ship.isDestroyed()) {
						SoundPlay.getInstance().play(SoundType.hit);
						this.ship.destroy();
						lives--;
						this.logger.info("Hit on player ship, " + lives
								+ " lives remaining.");
							this.clearItem();

					} else if (!this.ship.isDestroyed()) {
						shield = null;
					}
				}
			} else {
				collideEnemyShip(bullet,recyclable);
				collideDangerousSpecialShip(enemyShipSpecial, bullet, recyclable);
				collideDangerousSpecialShip(enemyShipDangerous, bullet, recyclable);
			}
		for(EXPItem expItem : this.expItems){
			if(expItem.getSpeed() == 0){
				if(checkCollision(expItem, this.ship) && !this.levelFinished){
					expRecyclable.add(expItem);
					expManager.plusExp(40);
					logger.info(Integer.toString(expManager.getExp()));
				}
			}
		}
		this.expItems.removeAll(expRecyclable);
		EXPItemPool.recycle(expRecyclable);
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	private void collideEnemyShip(Bullet bullet, Set<Bullet> recyclable){
		for (EnemyShipFormation formation :
				EnemyShipGenerator.shipFormationList) {
			for (EnemyShip enemyShip : formation)
      
				if (!enemyShip.isDestroyed()
						&& checkCollision(bullet, enemyShip)) {
          enemyShip.decreaseHP();
          if (enemyShip.isDestroyed()) {
            SoundPlay.getInstance().play(SoundType.enemyKill);
            this.score += enemyShip.getPointValue();
            this.shipsDestroyed++;

					expItems.add(EXPItemPool.getEXP(enemyShip.getPositionX(), enemyShip.getPositionY(), EXP_SPEED));

            if(enemyShip.getItemType() != null) {
              enemyShip.itemDrop(itemIterator);
              for(Item item : this.itemIterator)
                if(item != null) item.setSprite();
            }

            formation.destroy(enemyShip);
          }
					recyclable.add(bullet);
				}
		}
	}

	private void collideDangerousSpecialShip(EnemyShip enemyShip, Bullet bullet,Set<Bullet> recyclable){
		if (enemyShip != null
				&& !enemyShip.isDestroyed()
				&& checkCollision(bullet, enemyShip)) {
			SoundPlay.getInstance().play(SoundType.bonusEnemyKill);
			this.score += enemyShip.getPointValue();
			this.shipsDestroyed++;
			enemyShip.destroy();
			if(enemyShip == enemyShipDangerous){ enemyShipDangerousExplosionCooldown.reset();}
			else enemyShipSpecialExplosionCooldown.reset();
			recyclable.add(bullet);
		}
	}

	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, lives,
				this.bulletsShot, this.shipsDestroyed);
	}



	private void manageGetItem(Item item) {
		if (checkCollision(item, this.ship) && !this.levelFinished) {

			itemPool.add(item);
			item.setSprite();

			if (!item.getAcquired()) {
				if (itemPool.getItem().getItemType() == Item.ItemType.BulletSpeedItem) {
					this.returnCode = 0;
					this.clearItem();//효과초기화
					this.clearPointUp();
					this.itemInfoCooldown.reset();
					this.ship.setBulletSpeed(2 * ship.getBulletSpeed());

				} else if (itemPool.getItem().getItemType() == Item.ItemType.PointUpItem) {

					this.returnCode = 1;
					this.clearItem();//효과 초기화
					this.itemInfoCooldown.reset();
					for (EnemyShipFormation formation :
							EnemyShipGenerator.shipFormationList) {
						for (EnemyShip enemyShip : formation)
							enemyShip.setPointValue(2 * enemyShip.getPointValue());
					}

				} else if (itemPool.getItem().getItemType() == Item.ItemType.MachineGun) {

					LOGGER.info("Obtained MachineGun");

					this.clearItem();//효과 초기화

					this.ship.setShootingInterval(0.1 * this.ship.getShootingInterval());

				} else if (itemPool.getItem().getItemType() == Item.ItemType.ShieldItem) {

					this.returnCode = 2;
					this.clearItem();
					this.clearPointUp();
					this.itemInfoCooldown.reset();
					shield = new Shield(this.ship.getPositionX(), this.ship.getPositionY() - 3, this.ship);

				} else if (itemPool.getItem().getItemType() == Item.ItemType.SpeedUpItem) {

					this.returnCode = 3;
					this.clearItem();//효과 초기화
					this.clearPointUp();
					this.itemInfoCooldown.reset();
					this.ship.setShipSpeed(2 * this.ship.getSpeed());
				}
//			else if (item.getIsGet() == false &&
//					itemPool.getItem().getItemType() == Item.ItemType.EnemyShipSpeedItem) {
//
//					this.returnCode = 4;
//					this.clearItem();//효과 초기화
//					this.clearPointUp();
//					this.itemInfoCooldown.reset();
//					this.enemyShipFormation.setMovementSpeed(5 * this.enemyShipFormation.getMovementSpeed());
//
//			}
				else if (itemPool.getItem().getItemType() == Item.ItemType.ExtraLifeItem) {
					this.clearItem();
					this.clearPointUp();
					if (lives < 4) {
						lives++;
						this.returnCode = 5;
						this.itemInfoCooldown.reset();
					} else
						LOGGER.warning("생명 4개 초과");
				}
			}

			item.setAcquired(true);
		}
	}

	public void clearItem(){
		ship.setInitState();
		shield = null;
	}

	private void cleanItems() {
		Set<Item> recyclable = new HashSet<>();
		for (Item item : this.itemIterator) {
			item.update();
			if (item.getPositionY() < SEPARATION_LINE_HEIGHT
					|| item.getPositionY() > this.height)
				recyclable.add(item);
		}
		this.itemIterator.removeAll(recyclable);
	}

	public void clearPointUp(){
		for (EnemyShipFormation formation :
				EnemyShipGenerator.shipFormationList) {
			for (EnemyShip enemyShip : formation)
				enemyShip.setInitPointValue();
		}
	}

	private void operateShip(){
		boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
				|| inputManager.isKeyDown(KeyEvent.VK_D);
		boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
				|| inputManager.isKeyDown(KeyEvent.VK_A);

		boolean isRightBorder = this.ship.getPositionX()
				+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
		boolean isLeftBorder = this.ship.getPositionX()
				- this.ship.getSpeed() < 1;

		if (moveRight && !isRightBorder) {
			this.ship.moveRight();
			if(shield != null)
				shield.moveRight();
		}
		if (moveLeft && !isLeftBorder) {
			this.ship.moveLeft();
			if(shield != null)
				shield.moveLeft();
		}
		if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
			if (this.ship.shoot(this.bullets))
				this.bulletsShot++;
	}

	private void generateEnemyShip(){
		if(enemyGenerateCooldown.checkFinished()){
			enemyShipGenerator.GenerateEnemyShip(gameSettings);
			for (int i = 0; i < 3; i++) {
				if(enemyShipGenerator.isAlive(i)){
					EnemyShipGenerator.shipFormationList.get(i).attach(this);
				}
			}
			enemyGenerateCooldown.reset();
			this.logger.info("enemy ship appears");
		}
	}

	private void operateSpecialShip(){
		if(this.enemyShipSpecial.getPositionX() > this.width) {
			this.enemyShipSpecial = null;
			this.logger.info("The special ship has escaped");
		}else{
			if (!this.enemyShipSpecial.isDestroyed())
				this.enemyShipSpecial.move(2, 0);
			else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
				this.enemyShipSpecial = null;
		}
	}

	private void generateSpecialShip(){
		if(this.enemyShipSpecialCooldown.checkFinished()){
			this.enemyShipSpecial = new EnemyShip();
			this.enemyShipSpecialCooldown.reset();
			this.logger.info("A special ship appears");
		}
	}

	private void operateDangerousShip(){
		if (this.enemyShipDangerous.getPositionX() > this.width) {
			lives--;
			this.enemyShipDangerous = null;
			this.logger.info("The dangerous ship has escaped and you have lost lives");
		}else{
			if (!this.enemyShipDangerous.isDestroyed())
				this.enemyShipDangerous.move(1, 0);
			else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
				this.enemyShipDangerous = null;
		}
	}

	private void generateDangerousShip(){
		if (this.enemyShipDangerousCooldown.checkFinished()) {
			this.enemyShipDangerous = new EnemyShip(Color.BLUE);
			this.enemyShipDangerousCooldown.reset();
			this.logger.info("A dangerous ship appears");
		}
	}

	private boolean isEnemyInScreen(EnemyShip enemyShip){
		if(enemyShip.getPositionY() > this.height) return false;
		return enemyShip.getPositionX() >= 0
				&& enemyShip.getPositionX() <= this.width;
	}

}
