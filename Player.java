//import java.awt.event.MouseMotionListener;
//import java.awt.event.MouseEvent;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Graphics;
public class Player extends Tank// implements MouseMotionListener
{
   public Player(double xPos,double yPos)
   {
      super(xPos,yPos,"pics/player_base.png",0.5,"pics/player_barrel.png",0.05,1,3,500,3000);
   }
   public void move(int direction)
   {
      if(direction==0)
         yAccel=-ACCEL;
      else if(direction==1)
         xAccel=ACCEL;
      else if(direction==2)
         yAccel=ACCEL;
      else if(direction==3)
         xAccel=-ACCEL;
   }
   public void stop(int direction)
   {
      if(direction==4)
         yAccel=0;
      else if(direction==5)
         xAccel=0;
   }
   @Override
   public void update(GamePanel temp)
   {
      super.update(temp);
      Point mouse=MouseInfo.getPointerInfo().getLocation();
      Point window=temp.getLocationOnScreen();
      double turnAmountX=mouse.getX()-window.getX()-getCenterX();
      double turnAmountY=mouse.getY()-window.getY()-getCenterY();
      barrelRotation=Math.atan(turnAmountY/turnAmountX)+Math.PI/2;
      if(turnAmountX<0)
         barrelRotation+=Math.PI;
   }
   ///////////////////////////////////
   @Override
   public void draw(Graphics g)
   {
      super.draw(g);
   }
   /////////////////////////////////
   
   /*
   @Override
   public void mouseMoved(MouseEvent e)
   {
      double turnAmountX=e.getX()-getCenterX();
      double turnAmountY=e.getY()-getCenterY();
      rotation=Math.atan(turnAmountY/turnAmountX)+Math.PI/2;
      if(turnAmountX<0)
         rotation+=Math.PI;
      //mouseX=e.getX();
      //mouseY=e.getY();
   }
   @Override
   public void mouseDragged(MouseEvent e)
   {
      mouseMoved(e);
   }
   */
}