import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
public class GamePanel extends JPanel {
    
    private FlappyBird fb;
    private Bird bird;
    private ArrayList<Rectangle> rects;
    private Font titleFont, promptFont, scoreFont, pauseFont;
    private Image backgroundImage;

    
    public static final Color bg = new Color(0, 158, 158);
    public static final int PIPE_W = 50, PIPE_H = 30;
    private Image pipeHead, pipeLength;

    public GamePanel(FlappyBird fb, Bird bird, ArrayList<Rectangle> rects) 
    {
        this.fb = fb;
        this.bird = bird;
        this.rects = rects;
        titleFont  = new Font("Monospaced", Font.BOLD, 48);
        promptFont = new Font("Monospaced", Font.PLAIN, 24);
        scoreFont  = new Font("Arial", Font.BOLD, 18);
        pauseFont  = new Font("Arial", Font.BOLD, 48);

        try {
            pipeHead   = ImageIO.read(new File("78px-Pipe.png"));
            pipeLength = ImageIO.read(new File("pipe_part.png"));
            backgroundImage = ImageIO.read(new File("background.png"));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        
        g.drawImage(backgroundImage, 0, 0, FlappyBird.WIDTH, FlappyBird.HEIGHT, null);
        
        if (fb.paused() && fb.getScore() == 0) 
        {
            String title  = "WELCOME TO FLAPPY BIRD";
            String prompt = "PRESS SPACE TO START";

            g2d.setFont(titleFont);
            g2d.setColor(Color.WHITE);
            
            int tw = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (FlappyBird.WIDTH - tw) / 2, FlappyBird.HEIGHT / 2 - 20);
            g2d.setFont(promptFont);
            
            int pw = g2d.getFontMetrics().stringWidth(prompt);
            g2d.drawString(prompt, (FlappyBird.WIDTH - pw) / 2, FlappyBird.HEIGHT / 2 + 20);
            return;
        }
        bird.update(g);

        for (Rectangle r : rects) 
        {
            g2d.setColor(Color.GREEN);
            AffineTransform old = g2d.getTransform();
            g2d.translate(r.x + PIPE_W/2, r.y + PIPE_H/2);
            if (r.y < FlappyBird.HEIGHT/2) 
            {
                g2d.translate(0, r.height);
                g2d.rotate(Math.PI);
            }
            g2d.drawImage(pipeHead, -PIPE_W/2, -PIPE_H/2, PIPE_W, PIPE_H, null);
            g2d.drawImage(pipeLength, -PIPE_W/2, PIPE_H/2, PIPE_W, r.height, null);
            g2d.setTransform(old);
        }

        g2d.setFont(scoreFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + fb.getScore(), 10, 20);

        if (fb.paused() && fb.getScore() > 0) {
            String pausedText  = "PAUSED";
            String resumeText  = "PRESS SPACE TO CONTINUE";

            g2d.setFont(pauseFont);
            g2d.setColor(new Color(0, 0, 0, 170));
            
            int px = g2d.getFontMetrics().stringWidth(pausedText);
            g2d.drawString(pausedText, (FlappyBird.WIDTH - px) / 2, FlappyBird.HEIGHT / 2 - 20);
            g2d.setFont(promptFont);
            
            int rx = g2d.getFontMetrics().stringWidth(resumeText);
            g2d.drawString(resumeText, (FlappyBird.WIDTH - rx) / 2, FlappyBird.HEIGHT / 2 + 20);
        }
    }
}
