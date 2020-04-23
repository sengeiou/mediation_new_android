package com.example.meditationapp.javaActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.meditationapp.Api.ApiInterface;
import com.example.meditationapp.Api.RetrofitClientInstance;
import com.example.meditationapp.Custom_Widgets.CustomBoldtextView;
import com.example.meditationapp.ModelClasses.AllCatModelClasses.CategoryDataModelClass;
import com.example.meditationapp.ModelClasses.AllCatModelClasses.GetCategoryAndRecomendedModelClass;
import com.example.meditationapp.ModelClasses.AllCatModelClasses.RecomandedModelClass;
import com.example.meditationapp.R;
import com.example.meditationapp.adapter.AllCategoryAdapter;
import com.example.meditationapp.adapter.MusicAdapter;
import com.example.meditationapp.adapter.RecomendedAdapter;
import com.example.meditationapp.adapter.SoundScapeAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllCatAndRecomendedActivity extends BaseActivity {

    private RecyclerView categoryRV,recomendedRV;
    ApiInterface apiInterface;
    List<CategoryDataModelClass> categoryDataModelClasses;
    List<RecomandedModelClass> recomandedModelClasses;
    String cat_ID;

    String userID;
    String mypreference = "mypref", user_id = "user_id";

    private LinearLayout titleLL,my_recordings_ll;
    private CustomBoldtextView dummyText,recomended_txt,weight_title;
    private ProgressBar weight_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_two_fragment);

       categoryRV = findViewById(R.id.allCat_listRV);
       recomendedRV = findViewById(R.id.recomended_RV);

       titleLL = findViewById(R.id.title_weight_ll);
       dummyText = findViewById(R.id.titleBelow_txt);
        weight_progressBar = findViewById(R.id.weight_progressBar);
        my_recordings_ll = findViewById(R.id.ll_wl_my_recordings);
        recomended_txt = findViewById(R.id.recomended_txt);
        weight_title = findViewById(R.id.weight_title);

        titleLL.setVisibility(View.INVISIBLE);
        dummyText.setVisibility(View.INVISIBLE);
        my_recordings_ll.setVisibility(View.INVISIBLE);
        recomended_txt.setVisibility(View.INVISIBLE);
        categoryRV.setVisibility(View.INVISIBLE);
        recomendedRV.setVisibility(View.INVISIBLE);



        SharedPreferences preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        userID = preferences.getString(user_id, "");

        cat_ID = getIntent().getStringExtra("cat_id");

        GridLayoutManager grid_sound = new GridLayoutManager(AllCatAndRecomendedActivity.this, 3, RecyclerView.VERTICAL, false);
        categoryRV.setLayoutManager(grid_sound);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(AllCatAndRecomendedActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recomendedRV.setLayoutManager(linearLayoutManager);

        getData(userID,cat_ID);

    }

    public void getData(String userID, String cat_ID){

        apiInterface = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<GetCategoryAndRecomendedModelClass> call = apiInterface.getCatAndRecomended(userID,cat_ID);

        call.enqueue(new Callback<GetCategoryAndRecomendedModelClass>() {
            @Override
            public void onResponse(Call<GetCategoryAndRecomendedModelClass> call, Response<GetCategoryAndRecomendedModelClass> response) {

           if (response.isSuccessful()){

               GetCategoryAndRecomendedModelClass resource = response.body();

               assert resource != null;
               if (resource.getSuccess()){

                   titleLL.setVisibility(View.VISIBLE);
                   dummyText.setVisibility(View.VISIBLE);
                   my_recordings_ll.setVisibility(View.VISIBLE);
                   recomended_txt.setVisibility(View.VISIBLE);
                   categoryRV.setVisibility(View.VISIBLE);
                   recomendedRV.setVisibility(View.VISIBLE);
                   weight_progressBar.setVisibility(View.GONE);

                   categoryDataModelClasses = resource.getData().getAffirmation();
                   recomandedModelClasses = resource.getData().getRecomended();

                   AllCategoryAdapter allCategoryAdapter = new AllCategoryAdapter(AllCatAndRecomendedActivity.this,resource.getData().getAffirmation());
                   categoryRV.setAdapter(allCategoryAdapter);

                   final RecomendedAdapter recomendedAdapter = new RecomendedAdapter(AllCatAndRecomendedActivity.this,resource.getData().getRecomended());
                   recomendedRV.setAdapter(recomendedAdapter);

               }
               else {
                   Toast.makeText(AllCatAndRecomendedActivity.this, ""+resource.getMessages(), Toast.LENGTH_SHORT).show();
                   weight_progressBar.setVisibility(View.GONE);
               }

           }
           else {
               Toast.makeText(AllCatAndRecomendedActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
               weight_progressBar.setVisibility(View.GONE);
           }

            }

            @Override
            public void onFailure(Call<GetCategoryAndRecomendedModelClass> call, Throwable t) {
                Toast.makeText(AllCatAndRecomendedActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                weight_progressBar.setVisibility(View.GONE);

            }
        });

    }
}
