import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Font;

import java.awt.MouseInfo;
import java.awt.Point;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.util.ArrayList;
public abstract class GamePanel extends JPanel
{ 
   private boolean running=true,paused=true,nextLevelReady=false,hitFlashOn=true;
   private int frame=1;
   protected int ups,fps,score=10000,hitAlpha=0;
   protected GamePanel nextPanel;
   protected Player p;
   protected ArrayList<Enemy> enemies=new ArrayList<Enemy>();
   protected ArrayList<Shot> shots=new ArrayList<Shot>();
   protected String[][] board;
   protected final int INITIAL_ENEMIES=5;
   protected final String MOVE_UP="move up"
                        ,MOVE_DOWN="move down"
                        ,MOVE_LEFT="move left"
                        ,MOVE_RIGHT="move right"
                        ,STOP_VERTICAL="stop vertical"
                        ,STOP_HORIZONTAL="stop horizontal";
   protected GamePanel()
   {
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
      TanksSound.pickSong();
   }
   protected void gimp(String key,String name)
   {
      getInputMap().put(KeyStroke.getKeyStroke(key),name);
   }
   protected void gamp(String name,AbstractAction action)
   {
      getActionMap().put(name,action);
   }
   protected void setKeyBindings()
   {
      gimp("UP",MOVE_UP);
      gimp("W",MOVE_UP);
      gimp("DOWN",MOVE_DOWN);
      gimp("S",MOVE_DOWN);
      gimp("LEFT",MOVE_LEFT);
      gimp("A",MOVE_LEFT);
      gimp("RIGHT",MOVE_RIGHT);
      gimp("D",MOVE_RIGHT);
      
      gimp("released UP",STOP_VERTICAL);
      gimp("released W",STOP_VERTICAL);
      gimp("released DOWN",STOP_VERTICAL);
      gimp("released S",STOP_VERTICAL);
      gimp("released LEFT",STOP_HORIZONTAL);
      gimp("released A",STOP_HORIZONTAL);
      gimp("released RIGHT",STOP_HORIZONTAL);
      gimp("released D",STOP_HORIZONTAL);
         
      gamp(MOVE_UP,new MoveAction(0));
      gamp(MOVE_RIGHT,new MoveAction(1));
      gamp(MOVE_DOWN,new MoveAction(2));
      gamp(MOVE_LEFT,new MoveAction(3));
         
      gamp(STOP_VERTICAL,new MoveAction(4));
      gamp(STOP_HORIZONTAL,new MoveAction(5));
   }
   protected void loadNextLevel()
   {
      //nextPanel=this;
      Thread nextLevel=
         new Thread()
         {
            @Override
            public void run()
            {
               long testTime=System.currentTimeMillis();
               //nextPanel.restart();     //Tested- ~3.7 seconds but this freezes game while in thread(no multithreading) if synchronized
                                          //Found out problem- nextPanel=this; [above] stil creates reference to this
                                          //So nextPanel.restart; also calls this.restart();
               
               nextPanel=new TanksPanel();     //Tested- ~4.0-4.6 seconds
               //System.out.println(System.currentTimeMillis()-testTime);
            }
         };
      nextLevel.start();
   }
   public boolean isRunning()
   {
      return running;
   }
   public boolean isPaused()
   {
      return paused;
   }
   protected boolean isNextReady()
   {
      return nextLevelReady;
   }
   protected void nextIsReady()
   {
      nextLevelReady=true;
   }
   protected void nextNotReady()
   {
      nextLevelReady=false;
   }
   protected void pause()
   {
      paused=!paused;
   }
   public void setUps(int updates)
   {
      ups=updates;
   }
   public void setFps(int frames)
   {
      fps=frames;
   }
   protected void checkCollisions()
   {
      
      for(int i=0;i<shots.size();i++)
      {
         for(int j=0;j<shots.size();j++)
         {
            if(j==i)
               continue;
            else
               if(shots.get(i).checkCollision(shots.get(j)))
               {
                  shots.remove(i);
                  shots.remove(j-1);
                  //i--;
                  if(j<i)
                     i--;
                  i--;
                  break;
               }
         }
      }
      
      for(int i=0;i<shots.size();i++)
      {
         for(int j=0;j<enemies.size();j++)
         {
            if(shots.get(i).checkCollision(enemies.get(j)))
            {
               shots.remove(i);
               enemies.get(j).hit();
               if(!enemies.get(j).isAlive())
                  score+=100;
               
               TanksSound.damage();
               break;
            }
         }
         if(i<shots.size() && shots.get(i).checkCollision(p))
         {
            shots.remove(i);
            p.hit();
            if(!p.isAlive())
               score-=1000;
            hitAlpha=100;
            
            TanksSound.damage();
         }
      }
      for(int i=0;i<enemies.size();i++)
      {
         enemies.get(i).bump(p);
         p.bump(enemies.get(i));
         for(int j=0;j<enemies.size();j++)
         {
            if(i!=j)
            {
               enemies.get(i).bump(enemies.get(j));
               enemies.get(j).bump(enemies.get(i));
            }
         }
      }
   }
   public synchronized void render(double interpolation)
   {
      p.interpolate(interpolation);
      //e.interpolate(interpolation);
      for(Enemy enemy:enemies)
         enemy.interpolate(interpolation);
      for(Shot shot:shots)
         shot.interpolate(interpolation);
      repaint();
   }
   public synchronized void update()
   {
      //System.out.println(getWidth()+", "+getHeight());
      hitAlpha-=3;
      if(p.isAlive())
      {
         p.update(this);
         Shot temp=p.shoot();
         if(temp!=null)
            shots.add(temp);
      }
      else
      {
         p.updateExplosion();
         if(p.checkDeathTimer())
         {
            p.respawn();
            for(Enemy enemy:enemies)
               enemy.respawn();
            shots.clear();
            startLevel();
         }
      }
      for(int i=0;i<enemies.size();i++)
      {
         Enemy enemy=enemies.get(i);
         if(enemy.isAlive())
         {
            enemy.setPlayer(p);
            enemy.setPath(enemies.get(i).findPath(board));
            enemy.update(this);
         }
         else
         {
            enemy.updateExplosion();
            if(enemy.checkDeathTimer())
               enemy.respawn();
         }
      }
      for(int i=0;i<enemies.size();i++)
      {
         Shot temp=enemies.get(i).shoot();
         if(temp!=null)
            shots.add(temp);
      }
      for(int i=0;i<shots.size();i++)
      {
         Shot shot=shots.get(i);
         shot.update(this);
         if(!shot.isAlive())
            shots.remove(i);
      }
      checkCollisions();
      
      TanksSound.setDistance(findDistance());
   }
   private double findDistance()
   {
      double min=1000;
      for(int i=0;i<enemies.size();i++)
      {
         Enemy e=enemies.get(i);
         if(e.isAlive() && p.isAlive())
         {
            double temp=Math.sqrt(Math.pow(e.getCenterX()-p.getCenterX(),2)+Math.pow(e.getCenterY()-p.getCenterY(),2));
            if(temp<min)
               min=temp;
         }
      }
      return min;
   }
   protected int positionX(Graphics g,String text,double mult)
   {
      FontMetrics fm = g.getFontMetrics();
      return (int)(getWidth()*mult - fm.stringWidth(text)*0.5);
      //int centerY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
   }
   protected void drawUI(Graphics g)
   {
      for(Enemy e:enemies)
         e.drawHearts(g);
      p.drawHearts(g);
      
      g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,75));
      
      if(score<0)
         score=0;
      String text="SCORE: "+score;
      g.setColor(Color.WHITE);
      g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
      g.drawString(text,positionX(g,text,0.25),getHeight()-50);
      
      g.setColor(Color.WHITE);
      g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,20));
      g.drawString("UPS: "+ups,getWidth()-100,getHeight()-50);
      g.drawString("FPS: "+fps,getWidth()-100,getHeight()-25);
      
      if(p.isAlive())
      {
         try
         {
            Graphics2D g2= (Graphics2D)g.create();
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{50}, 50));
            g2.setColor(Color.BLUE);
            Point mouse=MouseInfo.getPointerInfo().getLocation();
            Point window=getLocationOnScreen();
            g2.drawLine((int)p.getCenterX(),(int)p.getCenterY(),(int)(mouse.getX()-window.getX()),(int)(mouse.getY()-window.getY()));
         }
         catch(Exception e)
         {
            
         }
      }
      
      if(hitFlashOn)
      {
         if(hitAlpha<0)
            hitAlpha=0;
         g.setColor(new Color(255,0,0,hitAlpha));
         g.fillRect(0,0,getWidth(),getHeight());
      }
   }
   @Override
   public synchronized void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      
      if(frame==2 && isPaused())
      {
         pause();
         TanksSound.play();
      }
      if(frame<3)
         frame++;
      
      for(int i=0;i<shots.size();i++)
         shots.get(i).draw(g);
      for(int i=0;i<enemies.size();i++)
         enemies.get(i).draw(g);
      p.draw(g);
   }
   protected void end()
   {
      running=false;
      TanksSound.silence();
   }
   public abstract GamePanel moveNextLevel();
   protected abstract void startLevel();
   private class MoveAction extends AbstractAction
   {
      private int direction;
      private MoveAction(int dir)
      {
         direction=dir;
      }
      @Override
      public void actionPerformed(ActionEvent e)
      {
         //Player p=(Player)tanks.get(0);
         if(direction<4)
            p.move(direction);
         else
            p.stop(direction);
      }
   }
}