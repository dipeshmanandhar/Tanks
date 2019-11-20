import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
public class MainMenuPanel extends GamePanel implements ActionListener
{
   private JButton play=new JButton("PLAY"),
                   exit=new JButton("EXIT");
   public MainMenuPanel()
   {
      setLayout(new GridBagLayout());
      addButtons();
      
      p=new Player(1300/2-40,1000/3-40);
      for(int i=0;i<INITIAL_ENEMIES;i++)
         enemies.add(new Enemy(Math.random()*(1500-40),Math.random()*(1000-40)));
      
      loadNextLevel();
   }
   private void addButtons()
   {
      //play.setSize(5000,300);
      //play.setMaximumSize(play.getSize());
      
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      
      play.addActionListener(this);
      exit.addActionListener(this);
      
      add(play,gbc);
      add(exit,gbc);
   }
   @Override
   public GamePanel moveNextLevel()
   {
      if(isNextReady())
      {
         if(nextPanel!=null)
         {
            nextNotReady();
            nextPanel.startLevel();
            TanksSound.silence();
         }
         return nextPanel;
      }
      return null;
   }
   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      
      g.setColor(Color.WHITE);
      g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,100));
      String text="TANKS";
      g.drawString(text,positionX(g,text,0.5),100);
      
      drawUI(g);
   }
   @ Override
   public void actionPerformed(ActionEvent e)
   {
      Object source=e.getSource();
      if(source==play)
      {
         TanksSound.selection();
         nextIsReady();
      }
      else if(source==exit)
      {
         TanksSound.selection();
         end();
      }
   }
   @Override
   protected void startLevel()
   {
      
   }
}