import java.awt.geom.Line2D;
import java.util.ArrayList;
public class Enemy extends Tank
{
   private Player player;
   private ArrayList<BoardBlock> path=new ArrayList<BoardBlock>();
   private final int TARGET_RANGE=200;
   public Enemy(double xPos,double yPos)
   {
      super(xPos,yPos,"pics/red_base.png",0.5,"pics/red_barrel.png",0.05,0.5,2,2000,10000);
   }
   @Override
   public void update(GamePanel temp)
   {
      super.update(temp);
      double xDiff=player.getCenterX()-getCenterX();
      double yDiff=player.getCenterY()-getCenterY();
      
      
      if(path.isEmpty())
      {
         simpleAI(xDiff,yDiff);
      }
      else
      {
         if(getBoardR()==path.get(0).getR() && getBoardC()==path.get(0).getC() && path.size()>1)
            path.remove(0);
         moveNextBlock();
      }
      
      barrelRotation=Math.atan(yDiff/xDiff)+Math.PI/2;
      if(xDiff<0)
         barrelRotation+=Math.PI;
   }
   public void setPlayer(Player p)
   {
      player=p;
   }
   public void checkAim(GameObject other)
   {
      Line2D aim=new Line2D.Double(getCenterX(),getCenterY(),player.getCenterX(),player.getCenterY());
      
      if(aim.intersects(other.getHitbox()))
         setShooting(false);
   }
   @Override
   public Shot shoot()
   {
      Shot temp=super.shoot();
      setShooting(true);
      return temp;
   }
   //////////////////////AI PATHFINDING METHODS START/////////////////////////
   
   private void simpleAI(double xDiff,double yDiff)
   {
      if(xSpeed<MAX_SPEED && xSpeed>-MAX_SPEED)
      {
         if(xDiff>TARGET_RANGE)
            xAccel=ACCEL;
         else if(xDiff<-TARGET_RANGE)
            xAccel=-ACCEL;
      }
      else
         xAccel=0;
      if(ySpeed<MAX_SPEED && ySpeed>-MAX_SPEED)
      {
         if(yDiff>TARGET_RANGE)
            yAccel=ACCEL;
         else if(yDiff<-TARGET_RANGE)
            yAccel=-ACCEL;
      }
      else
         yAccel=0;
   }
   private void moveNextBlock()
   {
      double xDiff=toPixelSpace(path.get(0).getC())-getCenterX();
      double yDiff=toPixelSpace(path.get(0).getR())-getCenterY();
      
      if(xSpeed<MAX_SPEED && xSpeed>-MAX_SPEED)
      {
         if(xDiff>5)
            xAccel=ACCEL;
         else if(xDiff<-5)
            xAccel=-ACCEL;
         else
            xAccel=0;
      }
      else
         xAccel=0;
      if(ySpeed<MAX_SPEED && ySpeed>-MAX_SPEED)
      {
         if(yDiff>5)
            yAccel=ACCEL;
         else if(yDiff<-5)
            yAccel=-ACCEL;
         else
            yAccel=0;
      }
      else
         yAccel=0;
   }
   public BoardBlock findPath(String[][] board)
   {
      if(board==null)
         return null;
      ArrayList<BoardBlock> open=new ArrayList<BoardBlock>(0);
      ArrayList<BoardBlock> closed=new ArrayList<BoardBlock>(0);
      
      open.add(new BoardBlock(getBoardR(),getBoardC()));
      
      while(open.size()>0)
      {
         BoardBlock current=findNextStep(open);
         open.remove(current);
         closed.add(current);
         for(int i=-1;i<=1;i+=2)
         {
            int currR=current.getR(),currC=current.getC();
            if(currR+i>=0 && currR+i<board.length && currC>=0 && currC<board[0].length && !board[currR+i][currC].equals("---"))
            {
               BoardBlock neighbor=new BoardBlock(current,current.getR()+i*2,current.getC());
               neighbor.setH(player.getBoardR(),player.getBoardC());
               neighbor.findF();
               
               if(neighbor.getR()==player.getBoardR() && neighbor.getC()==player.getBoardC())
                  return neighbor;
               
               if(!betterPathAlreadyExists(open,neighbor) && !betterPathAlreadyExists(closed,neighbor))
                  open.add(neighbor);
            }
            if(currR>=0 && currR<board.length && currC+i>=0 && currC+i<board[0].length && !board[currR][currC+i].equals("|"))
            {
               BoardBlock neighbor=new BoardBlock(current,current.getR(),current.getC()+i*2);
               neighbor.setH(player.getBoardR(),player.getBoardC());
               neighbor.findF();
               
               if(neighbor.getR()==player.getBoardR() && neighbor.getC()==player.getBoardC())
                  return neighbor;
               
               if(!betterPathAlreadyExists(open,neighbor) && !betterPathAlreadyExists(closed,neighbor))
                  open.add(neighbor);
            }
         }
      }
      return null;
   }
   public void setPath(BoardBlock n)
   {
      if(n!=null)
      {
         path.clear();
         while(n.hasParent())
         {
            path.add(0,n);
            n=n.getParent();
         }
         path.add(0,n);
      }
   }
   /*
   public void displayPath(BoardBlock lastNode)
   {
      while(lastNode.hasParent())
      {
         System.out.println(lastNode);
         lastNode=lastNode.getParent();
      }
      System.out.println(lastNode);
   }
   */
   private BoardBlock findNextStep(ArrayList<BoardBlock> open)
   {
      BoardBlock nextStep=open.get(0);
      for(BoardBlock n:open)
         if(n.getF()<nextStep.getF())
            nextStep=n;
      return nextStep;
   }
   private boolean betterPathAlreadyExists(ArrayList<BoardBlock> list,BoardBlock potential)
   {
      for(BoardBlock n:list)
         if(n.getR()==potential.getR() && n.getC()==potential.getC() && n.getF()<potential.getF())
            return true;
      return false;
   }
   //////////////////////AI PATHFINDING METHODS END/////////////////////////
}