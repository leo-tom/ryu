void gtkInit_entry(struct order *order){
    ryu_info *info = order->info;
    int argc = get_argc(info);
    char **argv = get_argv(info);
    gtk_init(&argc,&argv);
}
