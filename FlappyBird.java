import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.Timer;
import java.util.HashSet;
import java.util.Set;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.imageio.ImageIO;

public class FlappyBird implements ActionListener, KeyListener {
	private Image pipeImage;
    public static final int FPS = 60, WIDTH = 800, HEIGHT = 600;
    public static final int PIPE_W = 50;
    private int pipeSpeed, pipeGap;
    private static final int MIN_PIPE_HEIGHT = 50;
    private int highScore = 0;
    private Bird bird;
    private JFrame frame;
    private GamePanel panel;
    private ArrayList<Rectangle> rects = new ArrayList<>();
    private ArrayList<Rectangle> sensors = new ArrayList<>(); 
    private Set<Rectangle> scoredPipes = new HashSet<>();
    private int time = 0, scroll = 0;
    private boolean paused = true;
    private Timer timer;
    private Image menuBackground;
 
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new FlappyBird().go());
    }

    public void playSound(String soundFileName) 
    {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(soundFileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void go() 
    {
    	frame = new JFrame("Flappy Bird");
    	
        try 
        {
            menuBackground = ImageIO.read(new File("menu_bg.png"));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        showLevelSelectionDialog();

        frame.setVisible(true);
    	frame.addKeyListener(this);
        bird  = new Bird();
        panel = new GamePanel(this, bird, rects);
        frame.add(panel);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        timer = new Timer(1000 / FPS, this);
        timer.start();
    }

    private void showLevelSelectionDialog() 
    {
       ImageIcon bgIcon = new ImageIcon("menu_bg.png");
       JDialog dialog = new JDialog(frame, "Select Difficulty", true);
        dialog.setUndecorated(true);

        JLabel background = new JLabel(bgIcon);
        background.setLayout(new GridBagLayout());  

        
        String[] levels = { "Easy","Hard"};
        int[] speeds    = { 3, 5};
        int[] gaps      = { 150,100};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        
        for (int i = 0; i < levels.length; i++) 
        {
            JButton btn = new JButton(levels[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 24));
            final int idx = i;
            btn.addActionListener(e ->
            {
                pipeSpeed = speeds[idx];
                pipeGap   = gaps[idx];
                dialog.dispose();
            });
            background.add(btn, gbc);
        }

        dialog.setContentPane(background);
        dialog.pack();                              
        dialog.setLocationRelativeTo(frame);        
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        panel.repaint();
        if (paused) return;
        bird.physics();
        
      if (scroll % 90 == 0)
      {
            int maxH = FlappyBird.HEIGHT - pipeGap - MIN_PIPE_HEIGHT;
            int topH = MIN_PIPE_HEIGHT + (int)(Math.random() * (maxH - MIN_PIPE_HEIGHT + 1));
            int botH = FlappyBird.HEIGHT - pipeGap - topH;
            Rectangle topPipe    = new Rectangle(WIDTH, 0,      PIPE_W, topH);
            Rectangle bottomPipe = new Rectangle(WIDTH, topH + pipeGap, PIPE_W, botH);
            rects.add(topPipe);
            rects.add(bottomPipe);
            Rectangle sensor = new Rectangle(bottomPipe.x + bottomPipe.width,topPipe.height,pipeSpeed,pipeGap);
            sensors.add(sensor);
            }
          
      ArrayList<Rectangle> pipeRemove = new ArrayList<>();
        for (Rectangle r : rects)
        {
            r.x -= pipeSpeed;

            
            if (r.contains(bird.x, bird.y)|| bird.y > HEIGHT || bird.y < 0) 
            {
                gameOver();
                return;
            }
            if (r.x + r.width < 0) 
            {
                pipeRemove.add(r);
            }
        }
        rects.removeAll(pipeRemove);

        ArrayList<Rectangle> sensorRemove = new ArrayList<>();
        for (Rectangle s : sensors) {
            s.x -= pipeSpeed;
            if (s.contains(bird.x, bird.y))
            {
                time += 10;
                if (time > highScore)
                {
                    highScore = time;
                }
                sensorRemove.add(s);
            }
            else if (s.x < 0)
            {
                sensorRemove.add(s);
            }
        }
        sensors.removeAll(sensorRemove);
        scroll++;
    }

    private void gameOver() 
    {
    	playSound("hit.wav");   
    	paused = true;
        int choice = JOptionPane.showConfirmDialog(frame, "Game Over!\nYour score: " + time + "\n\nPlay again?","Game Over",JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) 
        {
            rects.clear();
            sensors.clear();
            bird.reset();
            time = scroll = 0;
            paused = false;
        } 
        else 
        {
            System.exit(0);
        }
    }
    @Override
    public void keyPressed(KeyEvent e) 
    {
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
        	playSound("jump.wav");
            bird.jump();
        } 
        else if (e.getKeyCode() == KeyEvent.VK_SPACE) 
        {
            paused = false;
        }
    }
    @Override public void keyReleased(KeyEvent e) 
    {}
    @Override public void keyTyped(KeyEvent e) 
    {}
    public int  getScore() 
    { return time; }
    public boolean paused() 
    { return paused; }
    public int getHighScore()
    { return highScore;}

}
