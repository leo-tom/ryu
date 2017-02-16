#include <dirent.h>
void ls_no_hide_ryu_entry(struct order *order){
    pse_stream *in = get_read_Stream(order,1);
    pse_stream *out = get_write_Stream(order,1);
    DIR *d;
    struct dirent *dir;
    char *folder;
    if(out==NULL){
        return;
    }
    if(in==NULL){
        folder = ".";
        d = opendir(folder);
    }else{
        folder = read_line(order,in);
		if(folder==NULL)
			return;
        d = opendir(folder);
    }
    if(d){
        while((dir=readdir(d))!=NULL){
            if(dir->d_name[0]!='.'){
                write_str_to_stream(out,dir->d_name);
                write_pse_stream(out,'\n');
            }
        }
		closedir(d);
    }else{
        fprintf(stderr, "Cannot open %s \n",folder==NULL ? "NULL" : folder );
        return;
    }
}
