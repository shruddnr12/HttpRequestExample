package com.jx372.httprequestexample.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;
import com.jx372.httprequestexample.R;
import com.jx372.httprequestexample.core.domain.Guestbook;
import com.jx372.httprequestexample.network.JSONResult;
import com.jx372.httprequestexample.network.SafeAsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onFetchGuestbookClick( View view ) {
        new FetchGuestbookListAsyncTask().execute();
    }

    // 통신 결과를 담을 Result 클래스
    private class JSONResultDeleteGuestbook extends JSONResult<Integer> {}

    // 통신 결과를 담을 Result 클래스
    private class JSONResultFetchGuestbokkList
            extends JSONResult< List<Guestbook> > {}

    /* 통신 내부 클래스 API 하나당 하나씩 */
    private class FetchGuestbookListAsyncTask extends SafeAsyncTask<List<Guestbook>> {

        @Override
        public List<Guestbook> call() throws Exception {

            //1. 요청 세팅
            String url = "http://192.168.1.39:8088/mysite03/guestbook/api/list";
            HttpRequest request = HttpRequest.get( url );
            //"name=안대혁&no=1"
            //request.contentType( HttpRequest.CONTENT_TYPE_FORM );
            //"{name:안대혁,no:1}"
            //request.contentType( HttpRequest.CONTENT_TYPE_JSON );
            request.accept( HttpRequest.CONTENT_TYPE_JSON );
            request.connectTimeout( 3000 );
            request.readTimeout( 3000 );

            //2. 요청
            int responseCode = request.code();

            //3. 응답 처리
            if( responseCode != HttpURLConnection.HTTP_OK ) {
                throw new RuntimeException( "Http Response Error :" + responseCode );
            }

            //4. GSON를 사용한 객체 생성
            Reader reader = request.bufferedReader();
            JSONResultFetchGuestbokkList jsonResult = new GsonBuilder().
                    create().
                    fromJson( reader, JSONResultFetchGuestbokkList.class );

            //5. 결과 에러 체크
            if( "fail".equals( jsonResult.getResult() ) ){
                throw new RuntimeException( jsonResult.getMessage() );
            }

            return jsonResult.getData();
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.e( "FetchGuestbooAsyncTask", "Exception:" + e );
            super.onException(e);
        }

        @Override
        protected void onSuccess(List<Guestbook> list) throws Exception {
            super.onSuccess(list);

            // 결과 처리
            for( Guestbook guestbook : list ) {
                System.out.println( guestbook );
            }
        }
    }
}
