package Raytracer.objects;

import Raytracer.cam;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class intake {
    public static void init(String driverName, cam c) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(driverName));
        String line;
        int modelID = 0;
        while ((line = reader.readLine()) != null) {
            String[] raw = line.split(" ");
            double[] num = new double[raw.length];
            if (!raw[0].equals("#")){

                //converts strings into doubles and intakes to cam object
                for (int i = 1; i < raw.length; i++) {
                    try { num[i] = Double.parseDouble(raw[i]);
                    } catch (Exception ignored) {}
                }
                switch(raw[0]) {
                    case     "eye" : c.cam.put( "eye" , new double[]{num[1], num[2], num[3]         });break;
		    case    "look" : c.cam.put( "look", new double[]{num[1], num[2], num[3]         });break;
                    case      "up" : c.cam.put( "up"  , new double[]{num[1], num[2], num[3]         });break;
                    case  "bounds" : c.cam.put( "bnds", new double[]{num[1], num[2], num[3], num[4] });break;
                    case     "res" : c.cam.put( "resl", new double[]{num[1], num[2]                 });break;
                    case       "d" : c.cam.put( "nefa", new double[]{num[1]                         });break;
                    case "ambient" : c.amb   =          new double[]{num[1], num[2], num[3]          };break;
		    case "recursionlevel" : c.depth =           (int)num[1]                           ;break;
		    case "sphere"  :
				    globe globe   = new globe();
                                    material mtrl = new material();

                                    //direct data intake
                                    globe.center  = new double[]{num[1], num[2], num[3]  };
                                    globe.radius  = num[4]                   ;
                                    mtrl.ka       = new double[]{num[5], num[6], num[7]  };
                                    mtrl.kd       = new double[]{num[8], num[9], num[10] };
                                    mtrl.ks       = new double[]{num[11],num[12],num[13] };
                                    mtrl.kr       = new double[]{num[14],num[15],num[16] };

                                    //indirect data intake
                                    mtrl.alpha    = 16;
                                    if (mtrl.ni  != 0){
                                        mtrl.tr = new double[]{1-mtrl.kr[0], 1-mtrl.kr[1], 1-mtrl.kr[2]};
                                    } else { mtrl.tr = new double[]{0, 0, 0}; }

                                    //completes globe object and places into list of objects
                                    globe.material = mtrl;
                                    c.objs.add(globe);
                                    break;
                    case   "light" :
                                    light l  = new light();
                                    l.p      = new double[]{num[1], num[2], num[3]};
                                    l.e      = new double[]{num[5], num[6], num[7]};
                                    c.lights.add(l);
                                    break;
                    case  "model" :
                                    model model   = new model();
                                    model.id      = modelID;

                                    if(num.length == 11){
                                        model.OBJname = raw[10];
                                    } else model.OBJname = raw[9];

                                    transform.trans(line, (model.id + ""));
                                    c.objs.add(model);
                                    modelID++;
                                    break;
				}
            }
        }
    }
}
