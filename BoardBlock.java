public class BoardBlock
{
   private BoardBlock parent;
   private int r,c;
   private double f,g,h;     //g=cost to node from start, h=distance to end
   public BoardBlock(int row,int col)     //for starting node ONLY
   {
      r=row;
      c=col;
      g=0;
      h=0;
      f=0;
   }
   public BoardBlock(BoardBlock p,int row,int col)     //for all other nodes
   {
      r=row;
      c=col;
      parent=p;
      g=parent.getG()+distanceTo(parent.getR(),parent.getC());
   }
   private double distanceTo(int row,int col)
   {
      return Math.sqrt(Math.pow(row-r,2)+Math.pow(col-c,2));
   }
   private double getG()
   {
      return g;
   }
   public int getR()
   {
      return r;
   }
   public int getC()
   {
      return c;
   }
   public void setH(int targetR,int targetC)
   {
      // Manhattan Distance
      h=targetR-r+targetC-c;
   }
   public void findF()
   {
      f=g+h;
   }
   public double getF()
   {
      return f;
   }
   public BoardBlock getParent()
   {
      return parent;
   }
   public boolean hasParent()
   {
      return parent!=null;
   }
   @Override
   public String toString()
   {
      return "("+r+","+c+")";
   }
}