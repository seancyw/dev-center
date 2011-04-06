using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using System.Windows.Shapes;
using System.Windows;
using System.Windows.Input;
namespace SpriteLightRock.Sprites
{
    class Module : SpriteObject
    {
        private class LocalDef
        {
            public const int FRE_FIX = 0x0100;
        }        

        public override int PreFixId
        {
            get
            {
                return LocalDef.FRE_FIX;
            }
        }
        public Module(int id, double x, double y, double w, double h)
            :base(id,x,y, w,h)
        {   
        }        
        public bool IsInside(Point p)
        {
            return p.X >= X && p.Y >= Y && p.X <= (X + Width) && p.Y <= (Y + Height);
        }
        public Rect GetBoundRect()
        {
            return new Rect(X, Y, Width, Height);
        }
        protected void OnMouseMove(MouseEventArgs e)
        {
            e.MouseDevice.OverrideCursor = Cursors.Hand;
        }        
    }
}
