using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace SpriteLightRock.Sprites
{
    class Frame : SpriteObject
    {
        private class LocalDef
        {
            public const int FRE_FIX = 0x0200;            
        }
        private List<FModule> _listFModules;


        public List<FModule> FModules
        {
            get { return _listFModules; }
            set { _listFModules = value; }
        }
        public Frame(
            int id,            
            double x,
            double y
        )
            : base(id,x,y)
        {
        }
        public bool Add(Module module,double x, double y)
        {
            Debug.Assert(module == null,"Could not add null module");
            //
            FModule fModule = new FModule(module, x, y);
            FModules.Add(fModule);
            return true;
        }
    }
}
