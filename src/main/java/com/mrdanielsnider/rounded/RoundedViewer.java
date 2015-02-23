package com.mrdanielsnider.rounded;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedViewer extends JPanel {
    private double radius;
    private double[] vectors = new double[8];
    private int draggingVector;

    private boolean isDragging() {
        return draggingVector != -1;
    }

    private void clearDragging() {
        draggingVector = -1;
    }

    private void setDragging(int i) {
        draggingVector = i;
    }

    public RoundedViewer(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double radius) {
        vectors = new double[]{x1, y1, x2, y2, x3, y3, x4, y4};
        this.radius = radius;
        clearDragging();
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                for (int i = 0; i < vectors.length / 2; i++) {
                    double x = vectors[i * 2];
                    double y = vectors[i * 2 + 1];
                    double sqrDist = (e.getX() - x) * (e.getX() - x) + (e.getY() - y) * (e.getY() - y);
                    if (sqrDist <= 50 * 50) {
                        setDragging(i);
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (isDragging()) {
                    vectors[draggingVector * 2] = e.getX();
                    vectors[draggingVector * 2 + 1] = e.getY();
                    clearDragging();
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (isDragging()) {
                    vectors[draggingVector * 2] = e.getX();
                    vectors[draggingVector * 2 + 1] = e.getY();
                    repaint();
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

    }

    void drawSegment(Graphics2D g, double x1, double y1, double x2, double y2) {
        Graphics2D g2 = (Graphics2D) g.create();
        g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        g2.setColor(Color.BLACK);
        drawPoint(g2, x1, y1);
        g2.setColor(Color.WHITE);
        drawPoint(g2, x2, y2);
        g2.dispose();
    }

    void drawPoint(Graphics2D g, double x, double y) {
        g.fillOval((int) x - 4, (int) y - 4, 8, 8);
    }


    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        Graphics2D g = (Graphics2D) graphics;
        drawSegment(g, vectors[0], vectors[1], vectors[2], vectors[3]);
        drawSegment(g, vectors[4], vectors[5], vectors[6], vectors[7]);

        ArcUtil.Arc arc = ArcUtil.roundedEdge(vectors[0], vectors[1], vectors[2], vectors[3], vectors[4], vectors[5], vectors[6], vectors[7], radius);

        drawPoint(g, arc.startX, arc.startY);
        g.drawOval((int) (arc.centerX - radius), (int) (arc.centerY - radius), (int) (2 * radius), (int) (2 * radius));

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(4));
        double startAngle = Math.acos((arc.startX - arc.centerX) / radius) * 180.0 / Math.PI;
        if(arc.startY > arc.centerY){
            startAngle *= -1;
        }
        g2.drawArc((int) (arc.centerX - radius), (int) (arc.centerY - radius), (int) (2 * radius), (int) (2 * radius), (int) startAngle, (int) arc.arcAngle);
        g2.dispose();

        g.setFont(Font.getFont(Font.MONOSPACED));
        g.drawString("centerX:  " + arc.centerX, 100, 100);
        g.drawString("centerY:  " + arc.centerY, 100, 115);
        g.drawString("startX:   " + arc.startX, 100, 130);
        g.drawString("startY:   " + arc.startY, 100, 145);
        g.drawString("arcAngle: " + arc.arcAngle, 100, 160);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public static void main(String[] args) {
        RoundedViewer roundedViewer2 = new RoundedViewer(500, 300, 300, 300, 400, 400, 400, 200, 20);
        JFrame frame = new JFrame();
        frame.add(roundedViewer2);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
