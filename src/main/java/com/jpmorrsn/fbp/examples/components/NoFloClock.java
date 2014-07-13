/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


public class NoFloClock extends com.jpmorrsn.fbp.engine.Component {

  // Experimental component based on standalone clock logic - see if it can run as component...

  private static int IMAGE_WIDTH = 800;

  private static int IMAGE_HEIGHT = 800;

  static JFrame frame = null;

  JComponent component = null;

  int sx, sy, mx, my, hx, hy;

  public void draw(final Graphics2D g2) {

    if (sx != 0 || sy != 0) {
      g2.setColor(Color.BLACK);
      g2.drawLine(400, 400, sx, sy);
    }
    if (mx != 0 || my != 0) {
      g2.setColor(Color.RED);
      g2.drawLine(400, 400, mx, my);
    }
    if (hx != 0 || hy != 0) {
      g2.setColor(Color.GREEN);
      g2.drawLine(400, 400, hx, hy);
    }

  }

  @Override
  public void execute() {
    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    component = new JComponent() {

      private static final long serialVersionUID = 1L;

      @Override
      public void paintComponent(final Graphics g) {
        draw((Graphics2D) g);

        component.repaint();
        frame.repaint();
      }

      @Override
      public Dimension getPreferredSize() {
        return new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT);
      }
    };
    //component.setVisible(false);
    component.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
    frame.add(component);
    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);
    JMenu menu = new JMenu("File");
    menu.setMnemonic('F');
    menuBar.add(menu);
    JMenuItem item = new JMenuItem("Save", 'S');
    menu.add(item);
    item.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
    frame.pack();
    frame.setVisible(true);

    //Date date = new Date();  // current time in UTC 
    Calendar calendar = new GregorianCalendar();

    //int year       = calendar.get(Calendar.YEAR);
    //int month      = calendar.get(Calendar.MONTH); // Jan = 0, dec = 11
    //int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); 
    //int dayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK);
    //int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
    //int weekOfMonth= calendar.get(Calendar.WEEK_OF_MONTH);

    int hour = calendar.get(Calendar.HOUR); // 12 hour clock
    //int hourOfDay  = calendar.get(Calendar.HOUR_OF_DAY); // 24 hour clock
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);

    double secs = second + 60 * minute + 3600 * hour;
    double theta = 270 + 6 * secs; // angle for second hand
    double phi = 270 + 6 * secs / 60;
    double psi = 270 + 30 * secs / 3600;

    while (true) {

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      sx = (int) (400 + 300 * Math.cos(Math.toRadians(theta)));
      sy = (int) (400 + 300 * Math.sin(Math.toRadians(theta)));
      mx = (int) (400 + 200 * Math.cos(Math.toRadians(phi)));
      my = (int) (400 + 200 * Math.sin(Math.toRadians(phi)));
      hx = (int) (400 + 100 * Math.cos(Math.toRadians(psi)));
      hy = (int) (400 + 100 * Math.sin(Math.toRadians(psi)));

      theta += 6;
      if (theta == 360) {
        theta = 0;
      }

      phi += 0.1;
      if (phi == 360) {
        phi = 0;
      }

      psi += 360 / (3600 * 12); // 12-hour clock face
      if (psi == 360) {
        psi = 0;
      }

      component.setVisible(true);
      component.repaint();

    }

  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.Component#openPorts()
   */
  @Override
  protected void openPorts() {
    // TODO Auto-generated method stub

  }

}