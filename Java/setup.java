class setup {
    static void init(cam c){
        c.eye    = c.cam.get("eye");
        c.CLv    = c.cam.get("look");
        c.CUPv   = c.cam.get("up");
        c.bs     = c.cam.get("bnds");
        c.umin   = c.bs[0];
        c.umax   = c.bs[1];
        c.vmin   = c.bs[2];
        c.vmax   = c.bs[3];
        c.nf     = c.cam.get("nefa");
        c.near   = c.nf[0];
        c.rs     = c.cam.get("resl");
        c.width  = c.rs[0];
        c.height = c.rs[1];

        c.W = math.unit(math.sub(c.eye, c.CLv));
        c.U = math.unit(math.cross(c.CUPv, c.W));
        c.V = math.cross(c.W, c.U);

        // Gets the necessary mtl files
        for (object cur : c.objs){
            if (cur instanceof model){
                model.intake((model) cur);
            }
        }
    }
}
