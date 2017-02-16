#include <stdio.h>
void read_ryu(struct order *order){
        int i;
        int c;
        pse_stream *out,*filename;
        char *buff;
        out = get_write_Stream(out,1);
        filename = get_read_Stream(out,1);
        if(out!=NULL&&filename==NULL){
            while((c=getchar())!=EOF){
                write_pse_stream(out,c);
            }
            return;
        }
        for(i=1;i<=8;i++){
            filename = get_read_Stream(order,i);
            out = get_write_Stream(order,i);
            if(filename==NULL||out==NULL){
                break;
            }else{
                while((buff = read_line(order,filename))!=NULL){
                    FILE *fp = NULL;
                    if(!strcmp(buff,"stderr")||!strcmp(buff,"standard error")){
                        fp = stderr;
                    }else if(!strcmp(buff,"stdout")||!strcmp(buff,"standard out")){
                        fp = stdout;
                    }else if((fp=fopen(buff,"r"))==NULL){
                        /*fprintf(stderr, "Failed to open %s\n",buff);*/
                    }else{
                        while((c=fgetc(fp))!=EOF){
                            write_pse_stream(out,c);
                        }
                        fclose(fp);
                        pse_stream_end_write(out);
                    }
                }
            }
        }
}
