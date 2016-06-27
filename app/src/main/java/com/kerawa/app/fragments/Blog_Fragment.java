package com.kerawa.app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kerawa.app.R;
import com.kerawa.app.helper.Blog_articles_loader;
import com.kerawa.app.utilities.Article;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShowHide;

import java.util.ArrayList;
import java.util.List;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;


/**
 * Fragment showing a solid background color
 *
 * @author Sotti https://plus.google.com/+PabloCostaTirado/about
 */
public class Blog_Fragment extends Fragment
{
    String category = "";
    private List<Article> movieList = new ArrayList<>();
    private  View loader;
    ImageView okButton;
    EditText searchText;
    private TextView statutext;

    public static Blog_Fragment newInstance(Bundle bundle)
    {
        Blog_Fragment colorFragment = new Blog_Fragment();

        if(bundle!=null)
        {
            colorFragment.setArguments(bundle);
        }

        return colorFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // The last two arguments ensure LayoutParams are inflated properly
        final View view;
        view = inflater.inflate(R.layout.articles_fragment, container, false);
        //initialise(view);
        view.getRootView().setBackgroundColor(Color.WHITE);
        ListView listView = (ListView) view.findViewById(R.id.list);
        loader=view.findViewById(R.id.loader);
        okButton= (ImageView) view.findViewById(R.id.okButton);
        searchText= (EditText) view.findViewById(R.id.searchText);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchText.getText().toString();
                launch_search(keyword);
            }
        });
        category = getArguments().getString("categorie");
        Krw_functions.pushOpenScreenEvent(getActivity(), Krw_functions.Current_country(getActivity()) + "/Activity_Blog/");
        statutext= (TextView) getActivity().findViewById(R.id.statusText);
        Blog_articles_loader les_article=new Blog_articles_loader();
        les_article.ctx=getActivity();
        les_article.listView= listView;
        les_article.statusText=statutext;
        les_article.movieList=movieList;
        les_article.v=loader;
        les_article.execute();

        new ShowHide(getActivity().findViewById(R.id.linearLayout5));

        return view;
    }

  private void launch_search(String keyword){
        Bundle bundle = new Bundle();
        bundle.putString("categorie", keyword);

        if (!keyword.equals("")){
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(String.format("Recherche: %s", keyword));
            }
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_frame, Blog_Fragment.newInstance(bundle)).commit();

        }else{

            Show_Toast(getActivity(), "Bien vouloir remplir du texte", false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowHide VD=new ShowHide(getActivity().findViewById(R.id.linearLayout5));
        (new ShowHide(getActivity().findViewById(R.id.statusText))).Hide();
        VD.Hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
