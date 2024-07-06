package com.example.tictactoe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
        Boolean gameActive = true;
        //Player Representation
        //0-X
        //1-o

    int activePlayer = 0;
    int [] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    // State Meanings
    // 0- 1
    // 1- X
    // 2- Blank

    int [][] winPositions = {{0,1,2}, {3,4,5}, {6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};

    public void playerTap(View view){
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());

        if(!gameActive){
            gameReset(view);
        }
        if(gameState[tappedImage]==2 ){
            gameState[tappedImage] = activePlayer;
            img.setTranslationY(-1000f);
            if (activePlayer == 0) {
                img.setImageResource(R.drawable.x);
                activePlayer = 1;
                TextView status = findViewById(R.id.status);
                status.setText("O's trun - Tap to Play");
            } else {
                img.setImageResource(R.drawable.o);
                activePlayer = 0;
                TextView status = findViewById(R.id.status);
                status.setText("X's trun - Tap to Play");
            }

            img.animate().translationYBy(1000f).setDuration(300);
        }

        //Check if anyone has won
        for(int[] winPosition:winPositions) {
            if(gameState[winPosition[0]]== gameState[winPosition[1]] &&
                    gameState[winPosition[1]]== gameState[winPosition[2]] &&
                    gameState[winPosition[0]] != 2){
                //Somebody has won - Find out whom
                gameActive = false;
                String winner_Str;
                if(gameState[winPosition[0]]==0){
                        winner_Str= "X has won";
                }
                else{
                    winner_Str= "O has won";
                }

                //Update the status bar when some one wins
                TextView status = findViewById(R.id.status);
                status.setText(winner_Str);


            }

            else{
                int count=0;
                for(int i =0; i<gameState.length; i++) {
                    if((gameState[i]==1 || gameState[i]==0) && (gameState[i]!=2))
                    {
                        count++;
                    }

                    if(count==9 && gameActive) {
                        String winner_Str;
                        winner_Str = "It's a Tie";
                        TextView status = findViewById(R.id.status);
                        status.setText(winner_Str);
                        gameActive= false;
                    }
                }
            }

        }


    }

    public void gameReset(View view){
        gameActive= true;
        activePlayer=0;
        for(int i =0; i<gameState.length; i++){
            gameState[i]=2;
        }
        ((ImageView)findViewById(R.id.imageView0)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView1)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView2)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView3)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView4)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView5)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView6)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView7)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView8)).setImageResource(0);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}