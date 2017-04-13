/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

/**
 *
 * @author user
 */
public class MainScreen implements Runnable {

    private JFrame frame;
    private Canvas canvas;
    private int canvasWidth, canvasHeight;

    private String title;
    private int width, height;

    private Thread thread; //this class is a runnable class so we need a Thread to run it 
    private boolean running; // store the state of our game

    //SOMETHING FOR RENDERING
    private BufferStrategy bufferStrategy;
    // like a hidden screen, you draw everything on this buffer and after drawing, it move all the things to actual screen
    // so we can prevent any flickering on our game

    private Graphics g;

    public MainScreen(String title, int width, int height) {

        this.title = title;
        this.width = width;
        this.height = height;
        this.canvasWidth = width - 400;
        this.canvasHeight = height - 25;

        createDisplay();

    }

    private void createDisplay() {

        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        canvas = new Canvas();
        canvas.setBounds(0, 0, canvasWidth, canvasHeight);
//        Dimension canvasSize = new Dimension(canvasWidth, canvasHeight);
//        canvas.setPreferredSize(canvasSize);
//        canvas.setMaximumSize(canvasSize);
//        canvas.setMinimumSize(canvasSize);
        canvas.setFocusable(false); //We want JFrame is the only thing that can have focus, basically allows the application to focus itself instead of the part that were drawn
        
//        frame.pack();
        setUpMenu(frame);
        setupButton(frame);
        setupInput(frame);
        setupLabel(frame);
        frame.add(canvas);
        frame.setVisible(true);
    }

    private void init() {

    }

    private void update() {

    }

    private void render() {
        bufferStrategy = this.canvas.getBufferStrategy();
        if (bufferStrategy == null) {
            this.canvas.createBufferStrategy(3);
            //we really do not need more than 3 buffers
            return;
        }
        // for now, we can sure that we have BufferStrategy so we can start drawing
        //Graphics object is like a "magic paintBrush" allows us draw things on canvas
        g = bufferStrategy.getDrawGraphics(); //create a paintBrush

        //Before we draw anything, we have to clear the screen
        g.clearRect(0, 25, canvasWidth, canvasHeight);
        //Start Drawing
        g.setColor(Color.BLACK);
        g.fillRect(0, 25, canvasWidth, canvasHeight);

        for (int i = 0; i < 100; i++) {
            drawDemo(g);
        }

        //End Drawing
        bufferStrategy.show(); //inform to Java that we're done drawing, switch the Buffer and display it to the screen
        g.dispose(); //make sure that Graphic object gets done with properly
    }

    int posX = 0, posY = 0;

    private void drawDemo(Graphics g) {
        float val1 = (float) Math.random() * 155 + 100;
        float val2 = (float) Math.random() * 155 + 100;
        float val3 = (float) Math.random() * 155 + 100;

        Color c = Color.getHSBColor(val1, val2, val3);
        g.setColor(c);
        if (posX >= canvasWidth) {
            posX = 0;
        }
        if (posY >= canvasHeight) {
            posY = height - canvasHeight;
        }
        posX++;
        posY++;
        g.fillRect(posX, posY, 10, 10);
    }

    @Override
    public void run() {
        init();
        //CONFIGURE THE FPS
        int fps = 60;
        double timePerTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        //end;

        while (running) {
            //TO RUN EXACTLY THE FPS THAT WE SET BEFORE
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            lastTime = now;

            if (delta >= 1) {
                update();
                render();
                delta--;

            }
            //END;
        }
        stop();
    }

    public synchronized void start() { //synchronized ?? basically for working with thread
        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //set up MainScreen bar
    public void setUpMenu(JFrame frame) {
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.setBounds(0, 0, width, height - canvasHeight);

        frame.add(jMenuBar);
        JMenu jMenuFile = new JMenu("File");
        jMenuBar.add(jMenuFile);
        JMenuItem jMenuItemExit = new JMenuItem("Exit");
        JMenuItem jMenuItemNewmap = new JMenuItem("Reset");
        JMenuItem jMenuItemStartcover = new JMenuItem("Start");

        jMenuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        jMenuItemNewmap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                mainScreen.resetNode();
            }
        });
        jMenuItemStartcover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                mainScreen.dfs(mainScreen.NodeList[1][1]);
//                mainScreen.clean();
//                mainScreen.calced = true;
            }
        });

        jMenuFile.add(jMenuItemNewmap);
        jMenuFile.add(jMenuItemStartcover);
        jMenuFile.add(jMenuItemExit);

    }

    //set up Control Panel: input field, button for set Power, set Obstacle, set Dirty
    public void setupInput(JFrame frame) {
        int posDrawY = height - canvasHeight + 50;
        
        JPanel algoPanel = new JPanel();
        algoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(),
                "Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.TOP, new java.awt.Font("Helvetica", 1, 16)));
        algoPanel.setLocation(canvasWidth + 5, posDrawY);
        algoPanel.setSize(width - canvasWidth - 10, 100);
        
        JTextField jTextFieldPower = new JTextField("Power robot");
        JButton btnSet = new JButton("Apply");
        jTextFieldPower.setBounds(canvasWidth + 10, algoPanel.getY() + 50, 90, 35);
        btnSet.setBounds(canvasWidth + 110, algoPanel.getY() + 50, 90, 35);

        jTextFieldPower.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent EVT) {
                String value = jTextFieldPower.getText();
                int l = value.length();
                if (EVT.getKeyChar() >= '0' && EVT.getKeyChar() <= '9') {
                    jTextFieldPower.setEditable(true);
                } else {
                    jTextFieldPower.setEditable(false);
                    jTextFieldPower.requestFocus();

                }
            }
        });
        btnSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float txt = Float.parseFloat(jTextFieldPower.getText());
//                mainScreen.robot.setPower(txt);
                JOptionPane.showMessageDialog(null, "Pin của Robot là " + txt + " %", "Infor", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // option quickly clean and set vet ban
        JButton btnMarkDirty = new JButton("Mark_VB");
        JButton btnMarkObstacle = new JButton("Mark_VC ");
        btnMarkDirty.setBounds(canvasWidth + 10, posDrawY + 100, 90, 35);
        btnMarkObstacle.setBounds(canvasWidth + 110, posDrawY + 100, 90, 35);
        btnMarkDirty.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                Vatcandidong vc = new Vatcandidong();
//                mainScreen.c = 1;
            }
        });

        btnMarkObstacle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                mainScreen.c = 2;
            }
        });

        frame.add(jTextFieldPower);
        frame.add(btnSet);
        frame.add(btnMarkDirty);
        frame.add(btnMarkObstacle);
        frame.add(algoPanel);
    }

    //set up Start Panel: button for Start and Quick Run
    public void setupButton(JFrame frame) {
        int posDrawY = height - canvasHeight + 200;
        
        JPanel algoPanel = new JPanel();
        algoPanel.setBorder(javax.swing.BorderFactory.
                createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(),
                        "Menu", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.TOP, new java.awt.Font("Helvetica", 1, 16)));
        algoPanel.setLocation(canvasWidth + 5, posDrawY);
        algoPanel.setSize(width - canvasWidth - 10, 80);
        
        JButton btnFind = new JButton("Start");
        JButton btnClear = new JButton("Quick");
        btnFind.setBounds(canvasWidth + 10, algoPanel.getY() + 30, 90, 35);
        btnFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                mainScreen.dfs(mainScreen.NodeList[1][1]);
//                mainScreen.clean();
//                mainScreen.calced = true;
            }
        });

        btnClear.setBounds(canvasWidth + 110, algoPanel.getY() + 30, 90, 35);
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                mainScreen.isfast = true;
//                mainScreen.dfs(mainScreen.NodeList[1][1]);
//                mainScreen.clean();
//                mainScreen.calced = true;
            }
        });

        frame.add(btnFind);
        frame.add(btnClear);
        frame.add(algoPanel);
    }

    public void setupLabel(JFrame fr) {
        int posDrawY = height - 200;
        
        JPanel algoPanel = new JPanel();
        algoPanel.setBorder(
                javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(),
                        "CONTROL PANEL", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.TOP, new java.awt.Font("Helvetica", Font.BOLD, 16)));
        algoPanel.setLocation(canvasWidth, 30);
        algoPanel.setSize(width - canvasWidth - 10, height - 10);
        algoPanel.setOpaque(false);
        
        JLabel lb1 = new JLabel("BÀI TẬP LỚN", JLabel.CENTER);
        lb1.setFont(new Font("Time New Roman", 3, 20));
        lb1.setForeground(Color.green);
        lb1.setLocation(canvasWidth, height - 150);
        lb1.setSize(width - canvasWidth - 10, 100);
        
        JLabel lb2 = new JLabel("HỆ CƠ SỞ TRI THỨC ", JLabel.CENTER);
        lb2.setFont(new Font("Time New Roman", 3, 20));
        lb2.setForeground(Color.green);
        lb2.setLocation(canvasWidth, height - 100);
        lb2.setSize(width - canvasWidth - 10, 100);
        
        
        
//        lb1.setBounds(canvasWidth + 10, posDrawY, 100, 30);
//        
//        lb2.setBounds(canvasWidth + 10, posDrawY + 30, 100, 30);
//        
//        lb2.setBackground(Color.white);

        fr.add(algoPanel);
        fr.add(lb1);
        fr.add(lb2);

    }
}
