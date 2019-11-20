import java.awt.geom.Rectangle2D;
public class Shot extends GameObject
{
   private GamePanel panel;
   private Tank owner;
   private long shotTime,pausedShotTime;
   private final int BUFFER_TIME=200;     //200 ms = 0.2 seconds
   public Shot(double xPos,double yPos,String imgFile,double maxSpeed,double rot,Tank shooter)
   {
      super(xPos,yPos,imgFile,0.1375,0,maxSpeed,2);
      rotation=rot;
      xSpeed=MAX_SPEED*Math.cos(rotation-Math.PI/2);
      ySpeed=MAX_SPEED*Math.sin(rotation-Math.PI/2);
      owner=shooter;
      shotTime=System.currentTimeMillis();
   }
   @Override
   public void update(GamePanel temp)
   {
      super.update(temp);
      panel=temp;
   }
   @Override
   protected double collide(double speed)
   {
      return -speed;
   }
   @Override
   public boolean bump(GameObject other)
   {
      nowBump(other);
      return futureBump(other);
   }
   @Override
   public void hit()
   {
      super.hit();
      rotation=Math.atan(ySpeed/xSpeed)+Math.PI/2;
      if(xSpeed<0)
         rotation+=Math.PI;
   }
   @Override
   public boolean isAlive()
   {
      try
      {
         return super.isAlive() && x>-getWidth() && x<panel.getWidth() && y>-getHeight() && y<panel.getHeight();
      }
      catch(NullPointerException e)
      {
         return true;
      }
   }
   @Override
   public boolean checkCollision(GameObject other)
   {
      /*
      if(super.checkCollision(other))
      {
         if(other.equals(owner) && System.currentTimeMillis()-shotTime<BUFFER_TIME)
               return false;
         return true;
      }
      return false;
      */
      //same thing below
      return super.checkCollision(other) && !(other.equals(owner) && System.currentTimeMillis()-shotTime<BUFFER_TIME);
   }
   public void pauseTimer()
   {
      pausedShotTime=System.currentTimeMillis()-shotTime;
   }
   public void unpauseTimer()
   {
      shotTime=System.currentTimeMillis()-pausedShotTime;
   }
   
   
   //solved by using "buffer time" above in checkCollision
   /*
   protected void move(double xOffset,double yOffset)
   {
      double tempX=Math.abs(xSpeed);
      double tempY=Math.abs(ySpeed);
      if(tempX>=tempY)
      {
         xOffset*=normalize(xSpeed)/xSpeed;
      }
      if(tempX<=tempY)
      {
         yOffset*=normalize(ySpeed)/ySpeed;
      }
      x+=xOffset/MAX_SPEED*xSpeed;
      y+=yOffset/MAX_SPEED*ySpeed;
      interpolate(0);
   }
   private int normalize(double num)
   {
      if(num>0)
         return (int)MAX_SPEED;
      else if(num<0)
         return (int)-MAX_SPEED;
      return 0;
   }*/
}