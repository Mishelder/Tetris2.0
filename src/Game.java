import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Game extends JFrame implements ActionListener {

    private JPanel panel;
    private final int Y = 20;
    private final int X = 15;
    private final int IMAGE_SIZE = 20;
    private ArrayList<Coord> coordBox = new ArrayList<>();
    private int[][] allCoord = new int[X][Y];
    private Box[][] allBox = new Box[X][Y];
    private Box currentColor;
    private Timer timer;
    private boolean inEnd = false;
    private int randomBox;
    private int currentFigure;
    private int nextFigure;
    private boolean game = true;
    private int rotate=0;
    private int row;
    private int score=0;
    private final int[][] gameOver ={
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,1,1,0,1,0,0,1,0,1,0,1,1,1,0,0,0,0,0,0},
            {0,1,0,0,1,0,1,1,0,1,0,1,1,0,0,0,0,0,0,0,0},
            {0,1,1,1,1,1,1,1,0,0,0,1,1,1,1,0,0,0,0,0,0},
            {0,1,0,1,1,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0},
            {0,0,1,1,1,0,1,1,0,0,0,1,1,1,1,0,0,0,0,0,0},
            {0,1,1,1,1,1,0,1,1,1,1,0,1,1,1,0,0,0,0,0,0},
            {0,1,0,0,1,1,0,1,1,0,0,0,1,0,1,0,0,0,0,0,0},
            {0,1,0,0,1,1,0,1,1,1,1,0,1,1,1,0,0,0,0,0,0},
            {0,1,0,0,1,1,1,1,1,0,0,0,1,1,0,0,0,0,0,0,0},
            {0,1,1,1,1,0,1,0,1,1,1,0,1,0,1,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
    };
    public static void main(String[] args) {
        new Game();
    }

    Game(){
        timer = new Timer(400,this);
        timer.start();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                int i = 0;
                if (checkBlockXR())
                    if (key == KeyEvent.VK_RIGHT) {
                        for (Coord coord : coordBox) {
                            coordBox.set(i, new Coord(coord.x + 1, coord.y));
                            i++;
                        }
                    }
                if (checkBlockXL())
                    if (key == KeyEvent.VK_LEFT) {
                        for (Coord coord : coordBox) {
                            coordBox.set(i, new Coord(coord.x - 1, coord.y));
                            i++;
                        }
                    }
                if (key == KeyEvent.VK_Q)
                   rotateLeft();
                if(key == KeyEvent.VK_S)
                    drop();
            }
        });
        initBox();
        create();
        initPanel();
        initFrame();
    }

    private void initFrame(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tetris");
        pack();
        setLocationRelativeTo(null);
        setIconImage(getImage("t","png"));
        setVisible(true);
    }

    private void initPanel(){
        panel= new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                if(!game){
                    for (int i = 0; i<X; i++) {
                        for (int j = 0; j < Y; j++) {
                            if (gameOver[i][j] == 1)
                                g.drawImage(getImage("game","png"), j * 20, i * 20, this);
                        }
                    }
                }
                if(game) {
                    for (Coord coord : coordBox)
                        g.drawImage(getImage(currentColor.name(),"png"), coord.x * IMAGE_SIZE,
                                coord.y * IMAGE_SIZE, this);

                    for (int i = 0; i < X; i++) {
                        for (int j = 0; j < Y; j++) {
                            if (allCoord[i][j] == 1)
                                g.drawImage(getImage(allBox[i][j].name(),"png"), i * IMAGE_SIZE, j * IMAGE_SIZE, this);
                        }
                    }
                }
                String scoreStr = String.valueOf(score);
                g.setColor(Color.WHITE);
                g.drawLine(300,0,300,400);
                g.drawString("Q - Rotate ",310,200);
                g.drawString("S - Drop",310,240);
                g.drawString("<- - Move left",310,260);
                g.drawString("-> - Move right",310,280);
                g.drawString("Score: "+scoreStr,320,90);
            }
        };
        panel.setPreferredSize(new Dimension(X*IMAGE_SIZE+100,Y*IMAGE_SIZE));
        panel.setBackground(Color.BLACK);
        add(panel);
    }

    private void create(){
        randomBox = (int)(Math.random()*7);
        switch(randomBox) {
            case 0:
                for (int i = 0; i < 4; i++)
                coordBox.set(i, new Coord(6 + i, 0));
            currentColor = Box.cube;
            break;
            case 1:
                for (int i = 1; i < 4; i++)
                    coordBox.set(i, new Coord(6 + i, 1));
                    coordBox.set(0, new Coord(8,0));
                currentColor = Box.cube;
                break;
            case 2:
                coordBox.set(0,new Coord(7,0));
                coordBox.set(1,new Coord(8,0));
                coordBox.set(2,new Coord(7,1));
                coordBox.set(3,new Coord(8,1));
                currentColor = Box.cube;
                break;
            case 3:
                for (int i = 1; i < 4; i++)
                    coordBox.set(i, new Coord(6 + i, 1));
                coordBox.set(0, new Coord(9,0));
                currentColor = Box.cube;
                break;
            case 4:
                for (int i = 1; i < 4; i++)
                    coordBox.set(i, new Coord(6 + i, 1));
                coordBox.set(0, new Coord(7,0));
                currentColor = Box.cube;
                break;
            case 5:
                coordBox.set(0,new Coord(8,0));
                coordBox.set(1,new Coord(7,0));
                coordBox.set(2,new Coord(7,1));
                coordBox.set(3,new Coord(6,1));
                currentColor = Box.cube;
                break;
            case 6:
                coordBox.set(0,new Coord(6,0));
                coordBox.set(1,new Coord(7,0));
                coordBox.set(2,new Coord(7,1));
                coordBox.set(3,new Coord(8,1));
                currentColor = Box.cube;
                break;
        }
        inEnd= false;
    }

    private void move(){
        int i =0;
        if(checkBlockY()){
            for (Coord coord1: coordBox) {
                setColor();
                allBox[coord1.x][coord1.y]=currentColor;
                allCoord[coord1.x][coord1.y]=1;
            }
            rotate=0;
            inEnd=true;
        }
        if(inEnd)
        for (Coord coord: coordBox){
            if(coord.y<=1)
                game = false;
        }

        if(!inEnd)
        for(Coord coord: coordBox) {
            coordBox.set(i, new Coord(coord.x, coord.y + 1));
            i++;
        }
    }

    private void initBox(){
        for(int i = 0;i<4;i++)
            coordBox.add(new Coord(0,0));
    }

    private Image getImage(String name,String format){
        String filename = "/img/"+name+"."+format;
        ImageIcon icon = new ImageIcon(getClass().getResource(filename));
        return  icon.getImage();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       if(game) {
           move();
           if (inEnd){
               create();
           }
       }
        checkBlockInLine();
        repaint();
    }

    private boolean checkBlockY(){
        for (Coord coord: coordBox) {
            if ((inRange(new Coord(coord.x,coord.y+1))&&allCoord[coord.x][coord.y + 1] == 1) || coord.y + 1 >= Y)
                return true;
        }
        return false;
    }

    private boolean checkBlockXR(){
        for (Coord coord: coordBox) {
            if ((!inRange(new Coord(coord.x+1,coord.y))||allCoord[coord.x+1][coord.y] == 1))
                return false;
        }
        return true;
    }

    private boolean checkBlockXL(){
        for (Coord coord: coordBox) {
            if ((!inRange(new Coord(coord.x-1,coord.y))||allCoord[coord.x-1][coord.y] == 1))
                return false;
        }
        return true;
    }
    private boolean inRange(Coord coord){
        return coord.y>=0&&coord.y<Y&&
                coord.x>=0&&coord.x<X;
    }
    private void rotateLeft(){
        if(checkBlockXL())
            if(checkBlockXR())
        switch(randomBox) {
            case 0:
                if(rotate==1){
                    coordBox.set(0, new Coord(coordBox.get(1).x+1, coordBox.get(1).y ));
                    coordBox.set(2, new Coord(coordBox.get(1).x-1, coordBox.get(1).y ));
                    coordBox.set(3, new Coord(coordBox.get(1).x-2, coordBox.get(1).y ));
                    rotate=0;
                    break;
                }
                if(rotate==0) {
                coordBox.set(0, new Coord(coordBox.get(1).x, coordBox.get(1).y + 1));
                coordBox.set(2, new Coord(coordBox.get(1).x, coordBox.get(1).y - 1));
                coordBox.set(3, new Coord(coordBox.get(1).x, coordBox.get(1).y - 2));
                rotate = 1;
                break;
                }

                break;
            case 1:
                if(rotate==3){
                    coordBox.set(3, new Coord(coordBox.get(0).x, coordBox.get(0).y ));
                    coordBox.set(0, new Coord(coordBox.get(1).x, coordBox.get(1).y ));
                    coordBox.set(1, new Coord(coordBox.get(2).x-1, coordBox.get(2).y));
                    rotate=0;
                    break;
                }
                if(rotate==2){
                coordBox.set(3, new Coord(coordBox.get(0).x, coordBox.get(0).y ));
                coordBox.set(0, new Coord(coordBox.get(1).x, coordBox.get(1).y ));
                coordBox.set(1, new Coord(coordBox.get(2).x, coordBox.get(2).y-1));
                rotate=3;
                    break;
                }
                if(rotate==1){
                    coordBox.set(3, new Coord(coordBox.get(0).x, coordBox.get(0).y ));
                    coordBox.set(0, new Coord(coordBox.get(1).x, coordBox.get(1).y ));
                    coordBox.set(1, new Coord(coordBox.get(2).x+1, coordBox.get(2).y));
                    rotate=2;
                    break;
                }
                if(rotate==0){
                    coordBox.set(3, new Coord(coordBox.get(0).x, coordBox.get(0).y ));
                    coordBox.set(0, new Coord(coordBox.get(1).x, coordBox.get(1).y ));
                    coordBox.set(1, new Coord(coordBox.get(2).x, coordBox.get(2).y+1 ));
                    rotate=1;
                    break;
                }

                break;
            case 3:
                if(rotate==3){
                    coordBox.set(1, new Coord(coordBox.get(1).x+1, coordBox.get(1).y ));
                    coordBox.set(2, new Coord(coordBox.get(1).x-1, coordBox.get(1).y ));
                    coordBox.set(3, new Coord(coordBox.get(1).x-2, coordBox.get(1).y ));
                    coordBox.set(0, new Coord(coordBox.get(1).x, coordBox.get(1).y-1));
                    rotate=0;
                    break;
                }
                if(rotate==2) {
                    coordBox.set(1, new Coord(coordBox.get(1).x, coordBox.get(1).y + 1));
                    coordBox.set(2, new Coord(coordBox.get(1).x, coordBox.get(1).y - 1));
                    coordBox.set(3, new Coord(coordBox.get(1).x, coordBox.get(1).y - 2));
                    coordBox.set(0, new Coord(coordBox.get(1).x+1, coordBox.get(1).y));
                    rotate = 3;
                    break;
                }
                if(rotate==1){
                    coordBox.set(1, new Coord(coordBox.get(1).x+1, coordBox.get(1).y ));
                    coordBox.set(2, new Coord(coordBox.get(1).x-1, coordBox.get(1).y ));
                    coordBox.set(3, new Coord(coordBox.get(1).x-2, coordBox.get(1).y ));
                    coordBox.set(0, new Coord(coordBox.get(3).x, coordBox.get(3).y+1));
                    rotate=2;
                    break;
                }
                if(rotate==0) {
                    coordBox.set(1, new Coord(coordBox.get(1).x, coordBox.get(1).y + 1));
                    coordBox.set(2, new Coord(coordBox.get(1).x, coordBox.get(1).y - 1));
                    coordBox.set(3, new Coord(coordBox.get(1).x, coordBox.get(1).y - 2));
                    coordBox.set(0, new Coord(coordBox.get(3).x-1, coordBox.get(3).y ));
                    rotate = 1;
                    break;
                }

                break;
            case 4:
                if(rotate==3){
                    coordBox.set(1, new Coord(coordBox.get(1).x+1, coordBox.get(1).y ));
                    coordBox.set(2, new Coord(coordBox.get(1).x-1, coordBox.get(1).y ));
                    coordBox.set(3, new Coord(coordBox.get(1).x-2, coordBox.get(1).y ));
                    coordBox.set(0, new Coord(coordBox.get(3).x, coordBox.get(3).y-1));
                    rotate=0;
                    break;
                }
                if(rotate==2) {
                    coordBox.set(1, new Coord(coordBox.get(1).x, coordBox.get(1).y + 1));
                    coordBox.set(2, new Coord(coordBox.get(1).x, coordBox.get(1).y - 1));
                    coordBox.set(3, new Coord(coordBox.get(1).x, coordBox.get(1).y - 2));
                    coordBox.set(0, new Coord(coordBox.get(3).x+1, coordBox.get(3).y));
                    rotate = 3;
                    break;
                }
                if(rotate==1){
                    coordBox.set(1, new Coord(coordBox.get(1).x+1, coordBox.get(1).y ));
                    coordBox.set(2, new Coord(coordBox.get(1).x-1, coordBox.get(1).y ));
                    coordBox.set(3, new Coord(coordBox.get(1).x-2, coordBox.get(1).y ));
                    coordBox.set(0, new Coord(coordBox.get(1).x, coordBox.get(1).y+1));
                    rotate=2;
                    break;
                }
                if(rotate==0) {
                    coordBox.set(1, new Coord(coordBox.get(1).x, coordBox.get(1).y + 1));
                    coordBox.set(2, new Coord(coordBox.get(1).x, coordBox.get(1).y - 1));
                    coordBox.set(3, new Coord(coordBox.get(1).x, coordBox.get(1).y - 2));
                    coordBox.set(0, new Coord(coordBox.get(1).x-1, coordBox.get(1).y ));
                    rotate = 1;
                    break;
                }
                break;
            case 5:
                if(rotate==1){
                    coordBox.set(3, new Coord(coordBox.get(1).x, coordBox.get(1).y));
                    coordBox.set(0, new Coord(coordBox.get(0).x+2, coordBox.get(0).y));
                    coordBox.set(1, new Coord(coordBox.get(0).x-1, coordBox.get(0).y ));
                    rotate=0;
                    break;
                }
                if(rotate==0) {
                    coordBox.set(0, new Coord(coordBox.get(1).x-1, coordBox.get(1).y ));
                    coordBox.set(1, new Coord(coordBox.get(0).x, coordBox.get(1).y+1));
                    coordBox.set(2, new Coord(coordBox.get(1).x+1, coordBox.get(1).y ));
                    coordBox.set(3, new Coord(coordBox.get(2).x, coordBox.get(2).y +1));
                    rotate = 1;
                    break;
                }
                break;
            case 6:
                if(rotate==1){
                    coordBox.set(3, new Coord(coordBox.get(2).x, coordBox.get(2).y));
                    coordBox.set(2, new Coord(coordBox.get(1).x, coordBox.get(1).y));
                    coordBox.set(1, new Coord(coordBox.get(2).x, coordBox.get(2).y-1));
                    coordBox.set(0, new Coord(coordBox.get(1).x-1, coordBox.get(1).y ));
                    rotate=0;
                    break;
                }
                if(rotate==0) {
                    coordBox.set(1, new Coord(coordBox.get(2).x, coordBox.get(2).y));
                    coordBox.set(0, new Coord(coordBox.get(1).x, coordBox.get(1).y+1 ));
                    coordBox.set(2, new Coord(coordBox.get(3).x, coordBox.get(3).y ));
                    coordBox.set(3, new Coord(coordBox.get(2).x, coordBox.get(2).y -1));
                    rotate = 1;
                    break;
                }
                break;
        }
    }

    private void setColor(){
        switch(randomBox) {
            case 0:
                currentColor = Box.lightgreen;
                break;
            case 1:
                currentColor = Box.purple;
                break;
            case 2:
                currentColor = Box.yellow;
                break;
            case 3:
                currentColor = Box.orange;
                break;
            case 4:
                currentColor = Box.blue;
                break;
            case 5:
                currentColor = Box.green;
                break;
            case 6:
                currentColor = Box.red;
                break;
        }
    }

    private void drop(){
        while (!checkBlockY()) {
            int j = 0;
            for (Coord coord : coordBox) {
                coordBox.set(j, new Coord(coord.x, coord.y + 1));
                j++;
            }
            if (checkBlockY())
                move();
        }
    }

    private void checkBlockInLine(){
        int countBlock = 0;
        for (int i =Y-1; i >= 0; i--) {
            for (int j = X-1; j >= 0 ; j--) {
                if(allCoord[j][i]==1)
                    countBlock++;
                if(countBlock==X){
                    row=i;
                    changeLine(row);
                }
            }
            countBlock=0;
        }
    }

    private void changeLine(int row){
        for (int i=X-1 ; i >= 0; i--) {
            for (int j = row; j > 0 ; j--) {
              allCoord[i][j]=allCoord[i][j-1];
              allBox[i][j]=allBox[i][j-1];

            }
        }
        score+=20;
    }




}
