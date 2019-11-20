import javax.swing.JFrame;
import java.awt.event.WindowEvent;
public class TanksDriver extends JFrame
{
   private GamePanel gamePanel=new MainMenuPanel();
   
   public TanksDriver()
   {
      super("Tanks");
      //pack();
      //setSize(800,800);
      //setLocationRelativeTo(null);
      setExtendedState(MAXIMIZED_BOTH); 
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      
      setContentPane(gamePanel);
      setVisible(true);
      gamePanel.requestFocus();
      
      TanksSound.initialize();
      
      runGameLoop();
      //gameLoop();
      
      //gamePanel.pause();
   }
   private void runGameLoop()
   {
      JFrame temp=this;
      Thread game=
         new Thread()
         {
            @Override
            public void run()
            {
               gameLoop();
               dispose();
               dispatchEvent(new WindowEvent(temp,WindowEvent.WINDOW_CLOSING));
            }
         };
      game.start();
   }
   private void gameLoop()
   {
      final int TARGET_UPS=60;
      final long TARGET_UPDATE_TIME=1000000000/TARGET_UPS;
      final int TARGET_FPS=60;
      final long TARGET_FRAME_TIME=1000000000/TARGET_FPS;
      long prevFrameTime=System.nanoTime();
      long accumulator=0;
      //for finding ups
      int updates=0;
      long runningUpdateTime=0;
      long prevUpdateTime=prevFrameTime;
      //for finding fps
      int frames=0;
      long runningFrameTime=0;
      while(gamePanel.isRunning())
      {
         GamePanel tempPanel=gamePanel.moveNextLevel();
         if(tempPanel!=null)
         {
            remove(gamePanel);
            //getContentPane().removeAll();
            gamePanel=tempPanel;
            getContentPane().invalidate();
            setContentPane(gamePanel);
            getContentPane().revalidate();
            
            //setVisible(true);
            gamePanel.requestFocus();
         }
         
         
         double interpolation=0;
         long now=System.nanoTime();
         accumulator+=now-prevFrameTime;
         runningFrameTime+=now-prevFrameTime;
         frames++;
         while(accumulator>TARGET_UPDATE_TIME)
         {
            if(!gamePanel.isPaused())
               gamePanel.update();
            accumulator-=TARGET_UPDATE_TIME;
            runningUpdateTime+=System.nanoTime()-prevUpdateTime;
            updates++;
            prevUpdateTime=System.nanoTime();
         }
         interpolation=(double)(System.nanoTime()-prevFrameTime)/TARGET_UPDATE_TIME;
         //System.out.println(interpolation);
         if(gamePanel.isPaused())
            interpolation=0;
         if(runningUpdateTime>=1000000000)
         {
            gamePanel.setUps(updates);
            updates=0;
            runningUpdateTime=0;
         }
         if(runningFrameTime>=1000000000)
         {
            gamePanel.setFps(frames);
            frames=0;
            runningFrameTime=0;
         }
         gamePanel.render(interpolation);
         prevFrameTime=now;
         while(now-prevFrameTime<TARGET_FRAME_TIME && now-prevFrameTime<TARGET_UPDATE_TIME)
         {
            Thread.yield();
            now=System.nanoTime();
         }
         /*
         if(tempPanel!=null)
         {
            if(gamePanel.isPaused())
            {
               gamePanel.pause();
               TanksSound.play();
            }
         }
         */
      }
   }
   public static void main(String[]arg)
   {
      new TanksDriver();
   }
}