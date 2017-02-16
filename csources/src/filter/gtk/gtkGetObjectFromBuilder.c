#include <gtk/gtk.h>
void gtkGetObjectFromBuilder_entry(struct order *order){
    pse_stream *gtkBuilderS = get_read_Stream(order,1);
    pse_stream *in = get_read_Stream_back(order,1);
    pse_stream *out = get_write_Stream(order,1);
    if(in==NULL||out==NULL){
        return;
    }
    char *objname;
    void *gtkBuilder = read_ptr(gtkBuilderS);
    if(gtkBuilder==NULL){
        return;
    }
    while((objname=read_line(order,in))!=NULL){
        write_ptr(out,gtk_builder_new_get_object(gtkBuilder,objname));
    }
}
