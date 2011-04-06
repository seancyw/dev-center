using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SpriteLightRock.Sprites
{
    class FModule : SpriteHightObject
    {
        //No need to have Id => just Index
        private new int Id
        {
            get{ return base.Id;}
            set{ base.Id = value;}
        }
        public int Index
        {
            get{return Id;}            
        }
        public FModule(
            int id,
            int referenceId,
            double x,
            double y
        )
            :base(id,referenceId,x,y)
        {

        }
        public FModule(            
            int referenceId
        )
            :base(referenceId,0,0)
        {

        }
    }
}
