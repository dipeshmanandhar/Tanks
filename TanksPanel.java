//Still need to:
//     -scoring:continues after respawns (both p deaths and wins and restarts)
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
public class TanksPanel extends GamePanel// implements MouseListener
{
   private Indestructible indestructibleEnemy;
   
   private long prevWinTime,levelStartTime,pausedLevelStartTime;
   private final int WIN_LAG=10000/*,ENEMY_SPAWN_TIME=10000*/,LEVEL_TIME_LIMIT=30000; //in miliseconds
   private boolean won=false;
   protected static final int WALL_WIDTH=10,WALLS_BETWEEN=10;
   private ArrayList<GameObject> walls=new ArrayList<GameObject>(0);
   
   private final String EXIT="exit"
                        ,PAUSE="pause";
   public TanksPanel()
   {
      /*
      setBackground(Color.GRAY.brighter());
      setKeyBindings();
      addMouseListener(
         new MouseAdapter()
         {
            @Override
            public void mousePressed(MouseEvent e)
            {
               p.setShooting(true);
            }
            @Override
            public void mouseReleased(MouseEvent e)
            {
               p.setShooting(false);
            }
         });
      */
      board=new String[8*2+1][11*2+1];
      initialize(board);
      setUp(board);
      placeObjects(board);
   }
   
   @Override
   protected void setKeyBindings()
   {
      super.setKeyBindings();
      gimp("ESCAPE",EXIT);
      gimp("P",PAUSE);
      
      gamp(EXIT,new MenuAction(0));
      gamp(PAUSE,new MenuAction(1));
   }
   @Override
   protected void checkCollisions()
   {
      super.checkCollisions();
      for(int i=0;i<walls.size();i++)
      {
         p.bump(walls.get(i));
         for(int j=0;j<enemies.size();j++)
         {
            enemies.get(j).bump(walls.get(i));
         }
         for(int j=0;j<shots.size();j++)
         {
            Shot shot=shots.get(j);
            if(shot.bump(walls.get(i)))
               shot.hit();
         }
      }
      if(indestructibleEnemy!=null && p.checkCollision(indestructibleEnemy))
      {
         p.hit();
         hitAlpha=100;
      }
   }
   private void checkAims()
   {
      for(int i=0;i<walls.size();i++)
         for(int j=0;j<enemies.size();j++)
            enemies.get(j).checkAim(walls.get(i));
   }
   //Changed so enemies still in ArrayList, but set to [!alive]
   /*
   private void spawnEnemies()
   {
      if(enemies.size()<INITIAL_ENEMIES)
      {
         for(int i=enemies.size();i<INITIAL_ENEMIES;i++)
         {
            Enemy temp;
            do
            {
               temp=new Enemy(((int)(Math.random()*(board[0].length-1)/2)*2+1)*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0,((int)(Math.random()*(board.length-1)/2)*2+1)*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0);
            }
            while(temp.checkCollision(p));
            enemies.add(temp);
         }
      }
   }*/
   //update called only when [!paused && running]
   @Override
   public synchronized void update()
   {
      super.update();
      if(!won)
         score--;
      
      if(System.currentTimeMillis()-levelStartTime>LEVEL_TIME_LIMIT && indestructibleEnemy==null)
         indestructibleEnemy=new Indestructible(Tank.toPixelSpace(getCol(board," o ")),Tank.toPixelSpace(getRow(board," o ")),p);
         
      if(indestructibleEnemy!=null)
      {
         indestructibleEnemy.setPlayer(p);
         indestructibleEnemy.update(this);
         if(!p.isAlive())
            indestructibleEnemy=null;
      }
      checkAims();
      
      //swap(board,p.getBoardR(),p.getBoardC(),getRow(board," o "),getCol(board," o "));
   }
   @Override
   public synchronized void render(double interpolation)
   {
      if(indestructibleEnemy!=null)
         indestructibleEnemy.interpolate(interpolation);
      super.render(interpolation);
   }
   /*
   private synchronized void restart()
   {
      enemies.clear();
      shots.clear();
      walls.clear();
      initialize(board);
      setUp(board);
      
      //long testTime=System.currentTimeMillis();
      placeObjects(board);     //Tested- takes about 3.8 seconds
      //System.out.println(System.currentTimeMillis()-testTime);
      System.out.println("RESTARTED");
   }
   */
   /////////////////////////// START MAZE METHODS //////////////////////////////////////
   private void setUp(String[][] board)
   {
      int r,c;
      ArrayList<Integer> wallRows=new ArrayList<Integer>(0);
      ArrayList<Integer> wallCols=new ArrayList<Integer>(0);
      if(Math.random()<0.5)
      {
         r=1;
         c=(int)(Math.random()*(board[0].length-1)/2)*2+1;
         //board[0][c]="   ";
         board[board.length-1][board[0].length-1-c]="   ";
      }
      else
      {
         r=(int)(Math.random()*(board.length-1)/2)*2+1;
         c=1;
         //board[r][0]=" ";
         board[board.length-1-r][board[0].length-1]=" ";
      }
      board[r][c]=" A ";
      if(r>1 && board[r-1][c].equals("---"))
      {
         wallRows.add(r-1);
         wallCols.add(c);
      }
      if(r<board.length-2 && board[r+1][c].equals("---"))
      {
         wallRows.add(r+1);
         wallCols.add(c);
      }
      if(c>1 && board[r][c-1].equals("|"))
      {
         wallRows.add(r);
         wallCols.add(c-1);
      }
      if(c<board[0].length-2 && board[r][c+1].equals("|"))
      {
         wallRows.add(r);
         wallCols.add(c+1);
      }
      while(wallRows.size()!=0)
      {
         int count=0;
         int index=(int)(Math.random()*wallRows.size());
         r=wallRows.get(index);
         c=wallCols.get(index);
         if(board[r][c].equals("---"))
         {
            if(board[r-1][c].equals("   "))
               count++;
            if(board[r+1][c].equals("   "))
               count++;
         }
         else if(board[r][c].equals("|"))
         {
            if(board[r][c-1].equals("   "))
               count++;
            if(board[r][c+1].equals("   "))
               count++;
         }
         if(count==1)
         {
            if(board[r][c].equals("---"))
            {
               board[r][c]="   ";
               if(board[r-1][c].equals("   "))
                  r--;
               else
                  r++;
            }
            else if(board[r][c].equals("|"))
            {
               board[r][c]=" ";
               if(board[r][c-1].equals("   "))
                  c--;
               else
                  c++;
            }
            if(r>1 && board[r-1][c].equals("---"))
            {
               wallRows.add(r-1);
               wallCols.add(c);
            }
            if(r<board.length-2 && board[r+1][c].equals("---"))
            {
               wallRows.add(r+1);
               wallCols.add(c);
            }
            if(c>1 && board[r][c-1].equals("|"))
            {
               wallRows.add(r);
               wallCols.add(c-1);
            }
            if(c<board[0].length-2 && board[r][c+1].equals("|"))
            {
               wallRows.add(r);
               wallCols.add(c+1);
            }
            board[r][c]=" 1 ";
         }
         wallRows.remove(index);
         wallCols.remove(index);
      }
      for(int row=0;row<board.length;row++)
         for(int col=0;col<board[0].length;col++)
            if(board[row][col].equals(" 1 "))
               board[row][col]="   ";
      board[getRow(board," A ")][getCol(board," A ")]=" o ";
   }
   private int getRow(String[][] board,String search)
   {
      for(int r=0;r<board.length;r++)
         for(int c=0;c<board[0].length;c++)
            if(board[r][c].equals(search))
               return r;
      return -1;
   }
   private int getCol(String[][] board,String search)
   {
      for(int r=0;r<board.length;r++)
         for(int c=0;c<board[0].length;c++)
            if(board[r][c].equals(search))
               return c;
      return -1;
   }
   private void swap(String[][] board,int r1,int c1,int r2,int c2)
   {
      String temp=board[r1][c1];
      board[r1][c1]=board[r2][c2];
      board[r2][c2]=temp;
   }
   private void initialize(String[][] board)
   {
      for(int r=0;r<board.length;r++)
      {
         for(int c=0;c<board[0].length;c++)
         {
            if(r%2==0)
            {
               if(c%2==0)
                  board[r][c]="+";
               else
                  board[r][c]="---";
            }
            else
            {
               if(c%2==0)
                  board[r][c]="|";
               else
                  board[r][c]="   ";
            }
         }
      }
   }
   //pre: board.length>=(offset*2)*2+1 & board[0].length>=(offset*2)*2+1
   private void placeObjects(String[][] board)
   {
      for(int r=0;r<board.length;r++)
      {
         for(int c=0;c<board[0].length;c++)
         {
            if(board[r][c].equals("+"))
               walls.add(new GameObject(c*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0,r*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0,"pics/wall.png",WALL_WIDTH/80.0,0,0,1));
            else if(board[r][c].equals("---"))
            {
               for(int i=0;i<WALLS_BETWEEN;i++)
                  walls.add(new GameObject((c-1)*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0+WALL_WIDTH*(i+1),r*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0,"pics/wall.png",WALL_WIDTH/80.0,0,0,1));
            }
            else if(board[r][c].equals("|"))
            {
               for(int i=0;i<WALLS_BETWEEN;i++)
                  walls.add(new GameObject(c*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0,(r-1)*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0+WALL_WIDTH*(i+1),"pics/wall.png",WALL_WIDTH/80.0,0,0,1));
            }
            else if(board[r][c].equals(" o "))
            {
               board[board.length-r-1][board[0].length-c-1]="END";
               p=new Player(c*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0,r*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0);
            }
         }
      }
      for(int i=0;i<INITIAL_ENEMIES;i++)
      {
         Enemy temp;
         do
         {
            temp=new Enemy(((int)(Math.random()*(board[0].length-1)/2)*2+1)*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0,((int)(Math.random()*(board.length-1)/2)*2+1)*WALL_WIDTH*(WALLS_BETWEEN+1)/2.0);
         }
         while(temp.checkCollision(p));
         enemies.add(temp);
         
         //System.out.println("enemy "+i+" created");
      }
   }
   /////////////////////////// END MAZE METHODS //////////////////////////////////////
   
   public GamePanel moveNextLevel()
   {
      if(isNextReady())
      {
         if(nextPanel!=null)
            nextNotReady();
         TanksSound.silence();
         return nextPanel;
      }
      if(nextPanel instanceof MainMenuPanel)
         return nextPanel;
      return null;
   }
   private void drawTimer(Graphics g,Color c,String text,int time,double xMult,int yPos)
   {
      if(time>0 && time<=10)
      {
         g.setColor(c);
         g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
         g.drawString(text,positionX(g,text,xMult),yPos);
         g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,75));
         text=time+"";
         g.drawString(text,positionX(g,text,xMult),yPos+80);
      }
   }
   @Override
   public synchronized void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      
      for(GameObject wall:walls)
         wall.draw(g);
      
      if(indestructibleEnemy!=null)
         indestructibleEnemy.draw(g);
      
      g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,75));
      
      
      if(won)
      {
         g.setColor(Color.GREEN.darker());
         String text="YOU WIN";
         g.drawString(text,positionX(g,text,0.5),80);
         
         drawTimer(g,Color.GREEN.darker(),"Next Level in...",(int)((WIN_LAG+prevWinTime-System.currentTimeMillis())/1000)+1,0.5,200);
         
         
         if(System.currentTimeMillis()-prevWinTime>WIN_LAG)
         {
            won=false;
            nextIsReady();
         }
      }
      else 
      {
         int r=p.getBoardR();
         int c=p.getBoardC();
         if(r>=0 && r<board.length && c>=0 && c<board[0].length && board[r][c].equals("END"))
         {
            won=true;
            prevWinTime=System.currentTimeMillis();
            
            loadNextLevel();
         }
         if(/*!running || */p.lives<=0)
         {
            g.setColor(Color.RED);
            String text="YOU LOSE";
            g.drawString(text,positionX(g,text,0.5),80);
         }
      }
      if(isPaused())
      {
         g.setColor(Color.RED);
         String text="PAUSED";
         g.drawString(text,positionX(g,text,0.5),160);
         for(Enemy enemy:enemies)
         {
            enemy.unpauseDeathTime();
            enemy.unpauseShotTime();
            enemy.unpauseExplosionUpdate();
         }
         for(Shot shot:shots)
            shot.unpauseTimer();
         p.unpauseDeathTime();
         p.unpauseShotTime();
         p.unpauseExplosionUpdate();
         unpauseLevelStartTime();
      }
      
      int deathTime=p.dispDeathTime();
      drawTimer(g,Color.BLUE,"Respawning in...",deathTime,0.2,80);
      deathTime=(int)((LEVEL_TIME_LIMIT+levelStartTime-System.currentTimeMillis())/1000)+1;
      drawTimer(g,Color.RED,"Indestructible in...",deathTime,0.8,80);
      
      /*
      deathTime=(ENEMY_SPAWN_TIME+prevEnemyDeathTime-System.currentTimeMillis())/1000+1;
      if(deathTime>0)
      {
         String text="Enemies in...";
         g.setColor(Color.RED);
         g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
         g.drawString(text,positionX(g,text,0.75),80);
         g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,75));
         text=deathTime+"";
         g.drawString(text,positionX(g,text,0.75),160);
      }
      */
      
      drawUI(g);
   }
   private void pauseLevelStartTime()
   {
      pausedLevelStartTime=System.currentTimeMillis()-levelStartTime;
   }
   private void unpauseLevelStartTime()
   {
      levelStartTime=System.currentTimeMillis()-pausedLevelStartTime;
   }
   @Override
   protected void startLevel()
   {
      levelStartTime=System.currentTimeMillis();
   }
   private class MenuAction extends AbstractAction
   {
      int code;
      private MenuAction(int key)
      {
         code=key;
      }
      @Override
      public void actionPerformed(ActionEvent e)
      {
         if(code==0)
         {
            nextPanel=new MainMenuPanel();
         }
         else if(code==1)
         {
            if(!isPaused())
            {
               for(Enemy enemy:enemies)
               {
                  enemy.pauseDeathTime();
                  enemy.pauseShotTime();
                  enemy.pauseExplosionUpdate();
               }
               for(Shot shot:shots)
                  shot.pauseTimer();
               p.pauseDeathTime();
               p.pauseShotTime();
               p.pauseExplosionUpdate();
               pauseLevelStartTime();
               
               TanksSound.silence();
            }
            else
               TanksSound.play();
            pause();
         }
      }
   }
}