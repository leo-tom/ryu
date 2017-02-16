#include <gtk/gtk.h>
void gtkBuilderReader_entry(struct order *order){
    pse_stream *in = get_read_Stream(order,1);
    pse_stream *out = get_write_Stream(order,1);
    if(in==NULL||out==NULL){
        return;
    }
    char *filename;
    while((filename=read_line(order,in))!=NULL){
        write_ptr(out,gtk_builder_from_file(filename));
    }
}
