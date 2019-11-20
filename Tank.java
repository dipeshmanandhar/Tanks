import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
public class Tank extends GameObject
{
   private final int TIME_BETWEEN_SHOTS;
   private final double ORIGINAL_X,ORIGINAL_Y;
   private final int ORIGINAL_LIVES;
   private long prevDeathTime,pausedDeathTime,
                prevExplosionUpdate,pausedExplosionUpdate,
                prevShotTime,pausedShotTime;
   private final int RESPAWN_TIME,EXPLOSION_UPDATE_TIME=80;     // in milliseconds
   private BufferedImage barrel,heart;
   private BufferedImage[] explosions=new BufferedImage[3];
   private int explosion=0;
   private boolean shooting;
   protected double barrelRotation;
   public Tank(double xPos,double yPos,String baseImg,double hbMult,String barrelImg,double accel,double maxSpeed,int maxLives,int shotTime, int respawnTime)
   {
      super(xPos,yPos,baseImg,hbMult,accel,maxSpeed,maxLives);
      barrel=buffer(barrelImg);
      explosions[0]=buffer("pics/explosion_1.png");
      explosions[1]=buffer("pics/explosion_2.png");
      explosions[2]=buffer("pics/explosion_3.png");
      TIME_BETWEEN_SHOTS=shotTime;
      ORIGINAL_X=xPos;
      ORIGINAL_Y=yPos;
      ORIGINAL_LIVES=maxLives;
      RESPAWN_TIME=respawnTime;
      
      heart=buffer("pics/heart.png");
   }
   @Override
   public void draw(Graphics g)
   {
      if(isAlive())
      {
         super.draw(g);
         Graphics2D g2=(Graphics2D)g;
         g2.rotate(barrelRotation,getCenterX(),getCenterY());
         g2.drawImage(barrel,(int)x,(int)y,barrel.getWidth(),barrel.getHeight(),null);
         g2.rotate(-barrelRotation,getCenterX(),getCenterY());
      }
      else if(System.currentTimeMillis()-prevDeathTime<1000)
      {
         BufferedImage temp=explosions[explosion];
         g.drawImage(temp,(int)x,(int)y,temp.getWidth(),temp.getHeight(),null);
         //updateExplosion();
      }
   }
   public void drawHearts(Graphics g)
   {
      for(int i=-1;i<lives-1;i++)
      {
         int xDraw=(int)x+(i*40);
         if(this instanceof Enemy)
            xDraw+=20;
         g.drawImage(heart,xDraw,(int)y-40,heart.getWidth(),heart.getHeight(),null);
      }
   }
   @Override
   public void update(GamePanel temp)
   {
      super.update(temp);
      //allow tanks to move form one side to another in window (messes up pathfinding for enemies if exit map)
      //x=(x+temp.getWidth())%temp.getWidth();
      //y=(y+temp.getHeight())%temp.getHeight();
      
      //block tanks from going through edges of window
      if(getHitbox().getX()<0)
         x=(getHitbox().getWidth()-getWidth())/2;
      else if(getHitbox().getX()+getHitbox().getWidth()>temp.getWidth())
         x=temp.getWidth()-getWidth()/2-getHitbox().getWidth()/2;
         
      if(getHitbox().getY()<0)
         y=(getHitbox().getHeight()-getHeight())/2;
      else if(getHitbox().getY()+getHitbox().getHeight()>temp.getHeight())
         y=temp.getHeight()-getHeight()/2-getHitbox().getHeight()/2;
      
      if(xSpeed!=0 || ySpeed!=0)
         rotation=Math.atan(ySpeed/xSpeed)+Math.PI/2;
      if(xSpeed<0)
         rotation+=Math.PI;
      
      if(xAccel==0)
      {
         if(xSpeed>ACCEL)
            xSpeed-=ACCEL/2;
         else if(xSpeed<-ACCEL)
            xSpeed+=ACCEL/2;
         else
            xSpeed=0;
      }
      if(yAccel==0)
      {
         if(ySpeed>ACCEL)
            ySpeed-=ACCEL/2;
         else if(ySpeed<-ACCEL)
            ySpeed+=ACCEL/2;
         else
            ySpeed=0;
      }
   }
   public boolean checkCollision(GameObject other)
   {
      if(other.isAlive() && isAlive())
         return super.checkCollision(other);
      else
         return false;
   }
   public boolean bump(GameObject other)
   {
      if(other.isAlive() && isAlive())
         return super.bump(other);
      else
         return false;
   }
   ///////////////////////////START SHOOTING METHODS////////////////////////////
   public Shot shoot()
   {
      long now=System.currentTimeMillis();
      if(shooting && now>prevShotTime+TIME_BETWEEN_SHOTS && lives>0)
      {
      //System.out.println((int)(getCenterX()*2/10/(10+1)));
      //System.out.println((int)((getCenterX()-35)/110)*2+1);
         prevShotTime=now;
         Shot temp=new Shot(x,y,"pics/bullet.png",5,barrelRotation,this);
         //temp.move(getHitbox().getWidth()-(temp.getHitbox().getX()-getHitbox().getX()),getHitbox().getHeight()-(temp.getHitbox().getY()-getHitbox().getY()));
         TanksSound.shot();
         return temp;
      }
      return null;
   }
   public void pauseShotTime()
   {
      pausedShotTime=System.currentTimeMillis()-prevShotTime;
   }
   public void unpauseShotTime()
   {
      prevShotTime=System.currentTimeMillis()-pausedShotTime;
   }
   public void setShooting(boolean state)
   {
      shooting=state;
   }
   ///////////////////////////END SHOOTING METHODS////////////////////////////
   
   @Override
   public void hit()
   {
      super.hit();
      if(lives==0)
      {
         prevDeathTime=System.currentTimeMillis();
         xSpeed=0;
         ySpeed=0;
         xAccel=0;
         yAccel=0;
         
         
         TanksSound.explosion();
      }
   }
   
   ///////////////////////////START DEATH TIMER METHODS////////////////////////////
   /*
   public void setDeathTime(long time)
   {
      prevDeathTime=time;
   }*/
   public long getDeathTime()
   {
      return prevDeathTime;
   }
   public boolean checkDeathTimer()
   {
      return System.currentTimeMillis()>prevDeathTime+RESPAWN_TIME;
   }
   public void respawn()
   {
      x=ORIGINAL_X;
      y=ORIGINAL_Y;
      lives=ORIGINAL_LIVES;
   }
   public int dispDeathTime()
   {
      if(lives<=0)
         return (int)((RESPAWN_TIME+prevDeathTime-System.currentTimeMillis())/1000)+1;
      return 0;
   }
   public void pauseDeathTime()
   {
      pausedDeathTime=System.currentTimeMillis()-prevDeathTime;
   }
   public void unpauseDeathTime()
   {
      prevDeathTime=System.currentTimeMillis()-pausedDeathTime;
   }
   ///////////////////////////END DEATH TIMER METHODS////////////////////////////
   
   
   ///////////////////////////BEGIN EXPLOSION METHODS////////////////////////////
   public void updateExplosion()
   {
      long now=System.currentTimeMillis();
      if(now-prevExplosionUpdate>EXPLOSION_UPDATE_TIME)
      {
         //System.out.println(now-prevExplosionUpdate);
         explosion=(explosion+1)%3;
         prevExplosionUpdate=now;
         //System.out.println(explosion);
      }
   }
   public void pauseExplosionUpdate()
   {
      pausedExplosionUpdate=System.currentTimeMillis()-prevExplosionUpdate;
      //if(!(this instanceof Player)){System.out.println(pausedExplosionUpdate);}
   }
   public void unpauseExplosionUpdate()
   {
      prevExplosionUpdate=System.currentTimeMillis()-pausedExplosionUpdate;
   }
   ///////////////////////////END EXPLOSION METHODS////////////////////////////
   
   //converts paint coordinates to board coords
   public int toBoardSpace(double pixels)
   {
      return (int)((pixels-35)/(TanksPanel.WALL_WIDTH*(TanksPanel.WALLS_BETWEEN+1)))*2+1;
   }
   public int getBoardC()
   {
      return toBoardSpace(getCenterX());
   }
   public int getBoardR()
   {
      return toBoardSpace(getCenterY());
   }
   protected static int toPixelSpace(int n)
   {
      return (n-1)/2*TanksPanel.WALL_WIDTH*(TanksPanel.WALLS_BETWEEN+1)+60+35;
   }
}