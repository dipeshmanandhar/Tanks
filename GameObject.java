import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
public class GameObject
{
   protected final double ACCEL,MAX_SPEED;
   protected double xSpeed,ySpeed;
   protected double x,y,rotation;
   protected double xAccel,yAccel;
   protected int lives;
   protected BufferedImage image;
   private Rectangle2D hitbox;
   public GameObject(double xPos,double yPos,String imgFile,double hbMult,double accel,double maxSpeed,int maxLives)
   {
      x=xPos;
      y=yPos;
      ACCEL=accel;
      MAX_SPEED=maxSpeed;
      image=buffer(imgFile);
      lives=maxLives;
      hitbox=new Rectangle2D.Double(getCenterX()-image.getWidth()*hbMult/2,getCenterY()-image.getHeight()*hbMult/2,image.getWidth()*hbMult,image.getHeight()*hbMult);
   }
   protected BufferedImage buffer(String filename)
   {
      try
      {
         return ImageIO.read(new File(filename));
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
      return null;
   }
   public void interpolate(double interpolation)
   {
      if(isAlive())
      {
         x+=xSpeed*interpolation;
         y+=ySpeed*interpolation;
         hitbox.setRect(getCenterX()-hitbox.getWidth()/2,getCenterY()-hitbox.getHeight()/2,hitbox.getWidth(),hitbox.getHeight());
      }
   }
   public void draw(Graphics g)
   {
      Graphics2D g2=(Graphics2D)g;
      g2.rotate(rotation,getCenterX(),getCenterY());
      g2.drawImage(image,(int)x,(int)y,image.getWidth(),image.getHeight(),null);
      g2.rotate(-rotation,getCenterX(),getCenterY());
      //g2.drawRect((int)hitbox.getX(),(int)hitbox.getY(),(int)hitbox.getWidth(),(int)hitbox.getHeight());
   }
   public void update(GamePanel temp)
   {
      if(xSpeed<=MAX_SPEED && xSpeed>=-MAX_SPEED)
         xSpeed+=xAccel;
      if(xSpeed>MAX_SPEED)
         xSpeed=MAX_SPEED;
      else if(xSpeed<-MAX_SPEED)
         xSpeed=-MAX_SPEED;
      if(ySpeed<=MAX_SPEED && ySpeed>=-MAX_SPEED)
         ySpeed+=yAccel;
      if(ySpeed>MAX_SPEED)
         ySpeed=MAX_SPEED;
      else if(ySpeed<-MAX_SPEED)
         ySpeed=-MAX_SPEED;
   }
   public boolean checkCollision(GameObject other)
   {
      if(other.isAlive())
         return hitbox.intersects(other.getHitbox());
      else
         return false;
   }
   protected double collide(double speed)
   {
      return 0;
   }
   protected boolean futureBump(GameObject other)
   {
      boolean fCollision=false;
      if(xSpeed!=0 || ySpeed!=0)
      {
         Rectangle2D futureHb=hitbox;
         
         double yTemp=hitbox.getY();
         double xTemp=hitbox.getX();
         futureHb.setRect(xTemp,yTemp+ySpeed,hitbox.getWidth(),hitbox.getHeight());
         if(futureHb.intersects(other.getHitbox()))
         {
            ySpeed=collide(ySpeed);
            fCollision=true;
         }
         futureHb.setRect(xTemp+xSpeed,yTemp,hitbox.getWidth(),hitbox.getHeight());
         if(futureHb.intersects(other.getHitbox()))
         {
            xSpeed=collide(xSpeed);
            fCollision=true;
         }
         if(!fCollision)
         {
            futureHb.setRect(xTemp+xSpeed,yTemp+ySpeed,hitbox.getWidth(),hitbox.getHeight());
            if(futureHb.intersects(other.getHitbox()))
            {
               xSpeed=collide(xSpeed);
               ySpeed=collide(ySpeed);
               fCollision=true;
            }
         }
      }
      hitbox.setRect(getCenterX()-hitbox.getWidth()/2+xSpeed,getCenterY()-hitbox.getHeight()/2+ySpeed,hitbox.getWidth(),hitbox.getHeight());
      return fCollision;
   }
   protected void nowBump(GameObject other)
   {
      if(checkCollision(other))
      {
         double tempX=x;
         Rectangle2D overlap=hitbox.createIntersection(other.getHitbox());
         if(xSpeed!=0)
            x-=xSpeed/Math.abs(xSpeed)*overlap.getWidth();
         if(ySpeed!=0)
            y-=ySpeed/Math.abs(ySpeed)*overlap.getHeight();
         else if(x==tempX)
         {
            if(getCenterX()>overlap.getX()+overlap.getWidth()/2)
               x+=overlap.getWidth();
            else if(getCenterX()<overlap.getX()+overlap.getWidth()/2)
               x-=overlap.getWidth();
            if(getCenterY()>overlap.getY()+overlap.getHeight()/2)
               y+=overlap.getHeight();
            else if(getCenterY()<overlap.getY()+overlap.getHeight()/2)
               y-=overlap.getHeight();
            else if(x==tempX)
            {
            // hitbox completely within other's hitbox
               x+=(int)(Math.random()*3-1)*other.getHitbox().getWidth();
               y+=(int)(Math.random()*3-1)*other.getHitbox().getHeight();
            }
         }
      }
      hitbox.setRect(getCenterX()-hitbox.getWidth()/2+xSpeed,getCenterY()-hitbox.getHeight()/2+ySpeed,hitbox.getWidth(),hitbox.getHeight());
   }
   public boolean bump(GameObject other)
   {
      boolean fCollision=futureBump(other);
      nowBump(other);
      return fCollision;
      //return futureBump(other);
   }
   public Rectangle2D getHitbox()
   {
      return hitbox;
   }
   public void hit()
   {
      lives--;
   }
   public boolean isAlive()
   {
      return lives>0;
   }
   protected double getCenterX()
   {
      return x+image.getWidth()/2;
   }
   protected double getCenterY()
   {
      return y+image.getHeight()/2;
   }
   protected double getWidth()
   {
      return image.getWidth();
   }
   protected double getHeight()
   {
      return image.getHeight();
   }
}