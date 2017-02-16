#include <gtk/gtk.h>
void gtkPixbufReader_Entry(struct order *order){
    pse_stream *in = get_read_Stream(order,1);
    pse_stream *out = get_write_Stream(order,1);
    pse_stream *err = get_write_Stream(order,2);
    if(in==NULL||out==NULL){
        return;
    }
    char *filename;
    while((filename=read_line(order,in))!=NULL){
        GError *er[]{NULL,NULL,NULL};
        GdkPixbuf *pix = gdk_pixbuf_new_from_file (filename,er);
        if(pix!=NULL){
            write_ptr(out,pix);
        }else{
            int i = 0;
            for(;er[i]!=NULL;i++){
                write_str_to_stream(err,er[i]->message);
                er[i] = NULL;
            }
        }

    }
}
