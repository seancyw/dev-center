using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SpriteLightRock.Sprites
{
    class Frame : SpriteHightObject
    {
        private class LocalDef
        {
            public const int FRE_FIX = 0x0200;            
        }
        private List<FModule> _listFModules;


        public override int PreFixId
        {
            get
            {
                return LocalDef.FRE_FIX;
            }
        }

        //
        public List<FModule> FModules
        {
            get { return _listFModules; }
            set { _listFModules = value; }
        }
        public Frame(
            int id,
            int referenceId,
            double x,
            double y
        )
            : base(id,referenceId,x,y)
        {
        }

    }
}
