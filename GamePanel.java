import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel {
    private FlappyBird fb;
    private Bird bird;
    private ArrayList<Rectangle> rects;
    public static final int PIPE_W = 50 ;   
    private Image pipeImage;                  
    private Image background, pauseBackground, welcome;
    private Font msgFont   = new Font("Monospaced", Font.BOLD, 24);
    private Font titleFont = new Font("Monospaced", Font.BOLD, 48);
    private Image welcomeBg = new ImageIcon("welcome_bg.png").getImage();
    
    public GamePanel(FlappyBird fb, Bird bird, ArrayList<Rectangle> rects) {
        this.fb    = fb;
        this.bird  = bird;
        this.rects = rects;
        try {
            background = ImageIO.read(new File("background.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            welcome = ImageIO.read(new File("welcome_bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        	pipeImage = ImageIO.read(new File("pipe.png"));
        	

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
     // welcome screen
        if (fb.paused() && fb.getScore() == 0) {
                    
            String title  = "HOW TO PLAY ??";
            String tip = "Use ↑ to flap!";
            String title2 = "PRESS SPACE TO START";
            g2.drawImage(welcomeBg,0, 0,getWidth(), getHeight(),null);
            g2.setFont(titleFont);
            g2.setColor(Color.BLACK);

            int tw = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (FlappyBird.WIDTH - tw) / 2, FlappyBird.HEIGHT / 2 - 20);
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            g2.setColor(Color.BLACK);

            
            int tipW = g2.getFontMetrics().stringWidth(tip);
            int tipY = FlappyBird.HEIGHT/2 + Bird.RAD + 40;
            g2.drawString(tip, (FlappyBird.WIDTH - tipW) / 2, tipY);
            g2.setFont(new Font("Monospaced", Font.BOLD,16));
            g2.setColor(Color.BLACK);
          

            g2.setFont(getFont());
            int pw = g2.getFontMetrics().stringWidth(title2);
            g2.drawString(title2, (FlappyBird.WIDTH - pw) / 2, FlappyBird.HEIGHT / 2 + 20);
            return;
        }

      
        if (background != null)
        {
            g2.drawImage(background, 0, 0, 
                         FlappyBird.WIDTH, FlappyBird.HEIGHT, null);
        }
        else
        {
            setBackground(new Color(0,158,158));
        }

        for (Rectangle r : rects) {
            AffineTransform old = g2.getTransform();
            
            g2.translate(r.x, r.y);

            if (r.y == 0) {
                
                g2.rotate(Math.PI,PIPE_W / 2.0,r.height / 2.0);
            }
           
            g2.drawImage(pipeImage,0, 0,PIPE_W,r.height,null );
            g2.setTransform(old);
        }
         bird.update(g);
        g2.setFont(msgFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + fb.getScore(), 10, 20);
        g2.drawString("High Score: " + fb.getHighScore(), 10, 40);

        if (fb.paused() && fb.getScore() > 0) 
        {
            if (pauseBackground != null) {
                g2.drawImage(pauseBackground, 0, 0,
                             FlappyBird.WIDTH, FlappyBird.HEIGHT, null);
            }
            
            g2.setColor(new Color(0,0,0,120));
            g2.fillRect(0,0,FlappyBird.WIDTH, FlappyBird.HEIGHT);
            g2.setFont(titleFont);
            g2.setColor(Color.YELLOW);
            String text = "PAUSED";
            int w = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (FlappyBird.WIDTH - w)/2,FlappyBird.HEIGHT/2 - 20);

            g2.setFont(msgFont);
            String prompt = "Press SPACE to resume";
            int pw = g2.getFontMetrics().stringWidth(prompt);
            g2.drawString(prompt, (FlappyBird.WIDTH - pw)/2, 
                          FlappyBird.HEIGHT/2 + 20);
        }
    }
}
