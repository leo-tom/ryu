#include <dirent.h>
void ls_no_hide_absolute_ryu_entry(struct order *order){
    pse_stream *in = get_read_Stream(order,1);
    pse_stream *out = get_write_Stream(order,1);
    DIR *d;
    struct dirent *dir;
    char *folder;
    char actualpath [PATH_MAX];
    char *ptr = ryumalloc(order,PATH_MAX);
    char *result;
    int i = 0;
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
                ptr[0] = '\0';
                    if(ptr==NULL) return;
                strcpy(ptr,folder);
                strcat(ptr,"/");
                strcat(ptr,dir->d_name);
                result = realpath(ptr, actualpath);
                if(result!=NULL){
                    write_str_to_stream(out,actualpath);
                    write_pse_stream(out,'\n');
                }
            }
        }
		closedir(d);
    }else{
        fprintf(stderr, "Cannot open %s \n",folder==NULL ? "NULL" : folder );
        return;
    }
}
