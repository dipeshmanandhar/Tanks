public class Indestructible extends GameObject
{
   Player player;
   public Indestructible(double centerX,double centerY,Player p)
   {
      super(centerX-80/2,centerY-80/2,"pics/indestructible.png",0.5,0.1,1,1);
      player=p;
   }
   @Override
   public void update(GamePanel temp)
   {
      super.update(temp);
      double xDiff=player.getCenterX()-getCenterX();
      double yDiff=player.getCenterY()-getCenterY();
      double mult=ACCEL/Math.sqrt(Math.pow(xDiff,2)+Math.pow(yDiff,2));
      xAccel=mult*xDiff;
      yAccel=mult*yDiff;
      
      //double mult=MAX_SPEED/Math.sqrt(Math.pow(xDiff,2)+Math.pow(yDiff,2));
      //xSpeed=mult*xDiff;
      //ySpeed=mult*yDiff;
   }
   ///////////From Enemy code////////////
   public void setPlayer(Player p)
   {
      player=p;
   }
   //////////////////////////////////////
}