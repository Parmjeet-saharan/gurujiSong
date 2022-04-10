package com.parmjeet.gurujisong;


import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

class MostPlay {
    Context context;
    ArrayList<songList> sList = new ArrayList<songList>();
    public MostPlay(Context context , ArrayList<songList> sList){
      this.context=context;
      this.sList=sList;
    }

    public void savedKept( ){
        String[] files =context.fileList();
        if(files.length>10){
            int extra=files.length-7;
                  for(int i=(files.length-1);(extra>0 && i>=0);i--){
                      if(files[i].startsWith("save_")){
                          File file=new File(context.getFilesDir(),files[i]);
                          file.delete();
                          extra--;
                      }
            }

        }


        }
}
