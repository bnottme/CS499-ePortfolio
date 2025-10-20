import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.AbstractAction;
import javax.swing.Timer;


/*
 * Resources Used
 * https://docs.oracle.com/javase/tutorial/uiswing/components/button.html
 * https://www.baeldung.com/java-resize-image
 * https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html
 * https://docs.oracle.com/javase/tutorial/uiswing/misc/timer.html
 */



public class SlideShow extends JFrame {

    private static final long serialVersionUID = 1L;

    
    private JPanel slidePane;   // shows the pictures
    private JPanel textPane;    // shows the captions
    private JPanel buttonPane;  // shows the buttons

    // lets us flip between slides and captions
    private CardLayout card;
    private CardLayout cardText;

    // buttons
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnPlayPause; // play/pause for autoplay

    // labels used while building slides
    private JLabel lblSlide;
    private JLabel lblTextArea;

   
    private final int SLIDE_COUNT = 5; // total number of slides
    private int currentIndex = 0; // 0..4 = current slide

    // we keep references so we can rescale the visible one
    private JLabel[] slideLabels = new JLabel[SLIDE_COUNT];
    private Image[] originalImages = new Image[SLIDE_COUNT];

    
    // where the images are located
    private String[] imagePaths = {
        "/resources/Germany.jpg",
        "/resources/Japan.jpg",
        "/resources/Italy.jpg",
        "/resources/Usa.jpg",
        "/resources/India.jpg"
    };

    // autoplay timer 
    private Timer autoTimer;
    private boolean isPlaying = false;

    
    
    public SlideShow() throws HeadlessException {
        initComponent();
    }

    private void initComponent() {
        // basic window setup
        setSize(800, 600);
        setLocationRelativeTo(null);
        setTitle("Top 5 Destinations SlideShow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 50));

        // make panels + layouts
        slidePane = new JPanel();
        textPane  = new JPanel();
        buttonPane = new JPanel();

        card = new CardLayout();
        cardText = new CardLayout();

        slidePane.setLayout(card);
        textPane.setLayout(cardText);

        // a little background color so captions stand out
        textPane.setBackground(Color.LIGHT_GRAY);

        // load all images once
        for (int i = 0; i < SLIDE_COUNT; i++) {
            originalImages[i] = loadImage(imagePaths[i]);
        }
        
        

        // build slides and captions
        for (int i = 1; i <= SLIDE_COUNT; i++) {
            // image label (centered)
            lblSlide = new JLabel();
            lblSlide.setHorizontalAlignment(JLabel.CENTER);
            lblSlide.setVerticalAlignment(JLabel.CENTER);

            // first time scale we will rescale again after layout/resize
            lblSlide.setIcon(scaleToFit(originalImages[i - 1], 800, 500));
            slideLabels[i - 1] = lblSlide;

            // caption label
            lblTextArea = new JLabel();
            lblTextArea.setText(getTextDescription(i));

            // add both "cards"
            slidePane.add(lblSlide, "card" + i);
            textPane.add(lblTextArea, "cardText" + i);
        }
        
        

        // center = slides
        getContentPane().add(slidePane, BorderLayout.CENTER);

        
        // bottom = captions on top of buttons
        JPanel south = new JPanel(new BorderLayout());
        south.add(textPane, BorderLayout.CENTER);

        
        // buttons row
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnPrev = new JButton("Previous");
        btnPrev.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { goPrevious(); }
        });
        buttonPane.add(btnPrev);

        btnPlayPause = new JButton("Play");
        btnPlayPause.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { toggleAutoplay(); }
        });
        buttonPane.add(btnPlayPause);

        btnNext = new JButton("Next");
        btnNext.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { goNext(); }
        });
        buttonPane.add(btnNext);
              
        
        neutralizeButtonSpace(btnPrev);
        neutralizeButtonSpace(btnPlayPause);
        neutralizeButtonSpace(btnNext);

        south.add(buttonPane, BorderLayout.SOUTH);
        getContentPane().add(south, BorderLayout.SOUTH);
        

        // autoplay timer
        autoTimer = new Timer(3500, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { goNext(); }
        });

        // keyboard shortcuts: <-  ->  Space
        setupKeyBindings();

        // rescale image if the window size changes
        slidePane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                rescaleVisibleSlide();
            }
        });

        // make sure first slide fits the panel after everything shows
        EventQueue.invokeLater(this::rescaleVisibleSlide);
    }

    // go to previous slide
    private void goPrevious() {
        currentIndex = (currentIndex - 1 + SLIDE_COUNT) % SLIDE_COUNT;
        card.previous(slidePane);
        cardText.previous(textPane);
        rescaleVisibleSlide();
    }

    
    
    // go to next slide
    private void goNext() {
        currentIndex = (currentIndex + 1) % SLIDE_COUNT;
        card.next(slidePane);
        cardText.next(textPane);
        rescaleVisibleSlide();
    }

    
    // start/stop autoplay
    private void toggleAutoplay() {
        if (isPlaying) {
            autoTimer.stop();
            btnPlayPause.setText("Play");
            isPlaying = false;
        } else {
            autoTimer.start();
            btnPlayPause.setText("Pause");
            isPlaying = true;
        }
    }

    
    // bind keys so the whole window listens (not just focused controls)
    private void setupKeyBindings() {
        JComponent root = (JComponent) getContentPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke("LEFT"),  "prev");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "next");
        im.put(KeyStroke.getKeyStroke("SPACE"), "toggle");

        am.put("prev", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override public void actionPerformed(ActionEvent e) { goPrevious(); }
        });
        am.put("next", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override public void actionPerformed(ActionEvent e) { goNext(); }
        });
        am.put("toggle", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override public void actionPerformed(ActionEvent e) { toggleAutoplay(); }
        });
    }

    // captions
    private String getTextDescription(int i) {
        String text = "";
        if (i == 1) {
            text = "<html><body><font size='3'>#1 Germany, Munich, Bath House.</font><br>Spectacular spa with amazing architecture and art.</body></html>";
        } else if (i == 2) {
            text = "<html><body><font size='3'>#2 Japan, Kansai.</font><br>Breathtaking nature with many natural spa's nearby.</body></html>";
        } else if (i == 3) {
            text = "<html><body><font size='3'>#3 Italy, Aosta valley.</font><br>Get away from the city and rejuvenate your mind with one of Italy's most beautiful landscapes.</body></html>";
        } else if (i == 4) {
            text = "<html><body><font size='3'>#4 United States, IL, Herrington Inn & Spa.</font><br>Take a break at this luxury spa with sensory therapies, massages, and herbal steams.</body></html>";
        } else if (i == 5) {
            text = "<html><body><font size='3'>#5 India, Yoga.</font><br>Sacred bath ceremonies and meditations to soothe your soul.</body></html>";
        }
        return text;
    }

    
    // load image from resources returns null if not found
    private Image loadImage(String path) {
        try {
            URL url = getClass().getResource(path);
            return (url == null) ? null : ImageIO.read(url);
        } catch (Exception e) {
            return null;
        }
    }

    
    // scale the image to fit inside maxW x maxH 
    private ImageIcon scaleToFit(Image src, int maxW, int maxH) {
        if (src == null || maxW <= 0 || maxH <= 0) return new ImageIcon();
        int iw = src.getWidth(null), ih = src.getHeight(null);
        if (iw <= 0 || ih <= 0) return new ImageIcon(src);

        double s = Math.min((double) maxW / iw, (double) maxH / ih);
        int w = Math.max(1, (int) Math.round(iw * s));
        int h = Math.max(1, (int) Math.round(ih * s));

        Image scaled = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    
    // whenever the window size changes or the card flips, refit the current image
    private void rescaleVisibleSlide() {
        int w = slidePane.getWidth();
        int h = slidePane.getHeight();
        if (w <= 0 || h <= 0) return;

        JLabel currentLabel = slideLabels[currentIndex];
        Image  src          = originalImages[currentIndex];

        if (currentLabel != null && src != null) {
            currentLabel.setIcon(scaleToFit(src, w, h));
            currentLabel.revalidate();
        }
    }

    
    // remove space from buttons so spacebar only controls play/pause
    private void neutralizeButtonSpace(JButton b) {
        b.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "none");
        b.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("SPACE"), "none");
    }

    
    // run
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                SlideShow ss = new SlideShow();
                ss.setVisible(true);
            }
        });
    }
}
