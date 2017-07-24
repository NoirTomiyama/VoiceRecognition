package com.lifeistech.android.voicerecognition;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.atilika.kuromoji.Tokenizer.Builder;
import org.atilika.kuromoji.Tokenizer.Mode;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private Button buttonStart;

    private int lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 言語選択 0:日本語、1:英語、2:オフライン、その他:General
        lang = 0;

        // 認識結果を表示させる
        textView = (TextView)findViewById(R.id.text_view);
        textView2 = (TextView)findViewById(R.id.text_view2);
        textView3 = (TextView)findViewById(R.id.text_view3);

        buttonStart = (Button)findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 音声認識を開始
                speech();
            }
        });
    }

    private void speech(){
        // 音声認識が使えるか確認する
        try {
            // 音声認識の Intent インスタンス
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            if(lang == 0){
                // 日本語
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString() );
            }
            else if(lang == 1){
                // 英語
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString() );
            }
            else if(lang == 2){
                // Off line mode
                intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            }
            else{
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            }

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力");
            // インテント発行
            startActivityForResult(intent, REQUEST_CODE);
        }
        catch (ActivityNotFoundException e) {
            textView.setText("No Activity " );
        }

    }

    // 結果を受け取るために onActivityResult を設置
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // 認識結果を ArrayList で取得
            ArrayList<String> candidates = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (candidates.size() > 0) {
                // 認識結果候補で一番有力なものを表示
                textView.setText(candidates.get(0));
                //getKatakana()メソッドで "null"だったら表示しない．
                if(!getKatakana(candidates.get(0)).matches(".*null.*")){
                    Log.d("getKatakana()",getKatakana(candidates.get(0)));
                    textView2.setText(getKatakana(candidates.get(0)));
                }else{
                    Log.d("getKatakana()",getKatakana(candidates.get(0)));
                    System.out.println("miss");
                }
                if(!zenkakuHiraganaToZenkakuKatakana(getKatakana(candidates.get(0))).matches(".*null.*")){
                    textView3.setText(zenkakuHiraganaToZenkakuKatakana(getKatakana(candidates.get(0))));
                }else{
                    System.out.println("miss2");
                }

//                textView2.setText(candidates.get(1));
//                textView3.setText(candidates.get(2));
                for(int i = 0; i < candidates.size() ;i++){
                    System.out.println("for文内" + candidates.get(i));
                }
            }
        }

        //textView2.setText(getKatakana("初日の出"));
        //System.out.println(getKatakana("東京特許許可局"));
        //System.out.println(zenkakuHiraganaToZenkakuKatakana("ジャヴァ・プログラミング"));

    }

    public static String getKatakana(String word) {
//        if (String.isNullOrEmpty(word))
//            return null;
        Builder builder = Tokenizer.builder();
        builder.mode(Mode.NORMAL);
        Tokenizer tokenizer = builder.build();
        List<Token> tokens = tokenizer.tokenize(word);
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens)
            sb.append(token.getReading());
        return sb.toString();
    }

    public static String zenkakuHiraganaToZenkakuKatakana(String s) {
        StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c >= 'ァ' && c <= 'ン') {
                sb.setCharAt(i, (char)(c - 'ァ' + 'ぁ'));
            } else if (c == 'ヵ') {
                sb.setCharAt(i, 'か');
            } else if (c == 'ヶ') {
                sb.setCharAt(i, 'け');
            } else if (c == 'ヴ') {
                sb.setCharAt(i, 'う');
                sb.insert(i + 1, '゛');
                i++;
            }
        }
        return sb.toString();
    }

}
