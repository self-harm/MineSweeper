import com.javarush.engine.cell.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;

public class MinesweeperGame extends Game{

    private static final int SIDE = 9;

    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags = 10;
    private int countMinesOnField;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    private void createGame(){
        //   isGameStopped = false;

        for(int x = 0; x < gameField.length; x++){
            for(int y = 0; y < gameField[x].length; y++){

                setCellColor(y, x, Color.PINK);
                boolean t = false;
                setCellValue(x, y, "");
                if (getRandomNumber(10)==1){
                    t=true;
                    countMinesOnField++;
                }

                gameField[y][x] = new GameObject(x, y, t);
            }
        }

        countMineNeighbors(); /*вызываем метод*/

        countFlags = countMinesOnField;

    }

    private void countMineNeighbors(){  /* добавляем посчитанных соседей в ячейки*/
        for (int y = 0; y < gameField.length ; y++) {
            for (int x = 0; x < gameField[y].length ; x++) {
                if (!gameField[y][x].isMine) {

                    for (GameObject object : getNeighbors(gameField[y][x]) ) {
                        if (object.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    public ArrayList<GameObject> getNeighbors(GameObject gameObject) { /*считаем соседей у минированных ячеек и добаляем
                                                                  в массив result;*/
        ArrayList<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    
    private void openTile(int x, int y){
        countClosedTiles--;
        boolean notZero = gameField[y][x].countMineNeighbors!=0;
        gameField[y][x].isOpen=true;
        setCellColor(x,y, Color.VIOLET);
        if(!gameField[y][x].isMine && gameField[y][x].isOpen){
            score += 5;
            setScore(score);
        }

        if(countClosedTiles == countMinesOnField && !gameField[y][x].isMine) {
            win();
        }

        else if(gameField[y][x].isMine){
            setCellValueEx(x,y,Color.RED, MINE);
            setCellColor(x,y, Color.RED);
            gameOver();
        }

        else if(notZero) {
            setCellNumber(x,y,gameField[y][x].countMineNeighbors);
        }

        else if(!notZero){
            setCellValue(x,y,"");
            List<GameObject> list = new ArrayList<>();
            list=getNeighbors(gameField[y][x]);
            for(int i=0; i<list.size();i++){
                int Xx=list.get(i).x;
                int Yy=list.get(i).y;
                if(!list.get(i).isOpen){
                    openTile(Xx,Yy);
                }
            }
        }

        else if(isGameStopped == true){return;}
        else if(gameField[x][y].isOpen){return;}
        else if(gameField[x][y].isFlag){
            return;
        }
    }
    
    private void markTile (int x, int y){
        if(isGameStopped == false){
            if(gameField[y][x].isOpen) return;
            if(countFlags==0 && !gameField[y][x].isFlag) return;
            else if(!gameField[y][x].isFlag) {
                gameField[y][x].isFlag=true;
                countFlags--;
                setCellValue(x,y,FLAG);
                setCellColor(x,y,Color.ORANGE);}
            else if(gameField[y][x].isFlag){
                gameField[y][x].isFlag = false;
                countFlags++;
                setCellValue(x, y, "");
                setCellColor(x,y,Color.PINK);
            }
        }
    }
    
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog( Color.GRAY, "The game is over!", Color.BLACK, 55);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog( Color.GRAY, "Wow! You won!", Color.BLACK, 55);
    }

    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE*SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        //clearField();
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y){ //В методе onMouseLeftClick(int, int) должен быть вызван метод openTile(int, int).
        // super.onMouseLeftClick(x, y);
        // openTile(x,y);
        if (isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y){ //Метод onMouseRightClick(int, int) должен вызывать метод markTile(int, int).
        super.onMouseRightClick(x, y);
        markTile(x,y);
    }
}
