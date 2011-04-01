using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
namespace SpriteLightRock.Sprite
{
    [StructLayout(LayoutKind.Explicit)]
    class Module
    {
        enum ModuleType : byte
        {
            Image,
            Rectangle,
            Ellipse,
            Triangle
        }                
        //
        [FieldOffset(0)]
        private ModuleType _kind;
        [FieldOffset(1)]
        private int _id;
        [FieldOffset(5)]
        private int _x;
        [FieldOffset(9)]
        private int _y;
        //Image & Rectangle & Ellipse
        [FieldOffset(13)]
        private int _w;
        [FieldOffset(17)]
        private int _h;        
        //Triangle
        [FieldOffset(5)]
        private int _x1;
        [FieldOffset(9)]
        private int _y1;
        [FieldOffset(13)]
        private int _x2;
        [FieldOffset(17)]
        private int _y2;
        [FieldOffset(21)]
        private int _x3;
        [FieldOffset(25)]
        private int _y3;

        private ModuleType Type
        {
            get { return _kind; }
            set { _kind = value; }
        }
        public int Id
        {
            get { return _id;}
            set { _id = value; }
        }
        public int X {
            get { return _x; }
            set{ _x = value; } 
        }
        
        public int Y { 
            get{return _y;}
            set { _y = value; }
        }
        
        public int Width {
            get { return _w; }
            set { _w = value; } 
        }
        
        public int Height {
            get { return _h; }
            set {_h = value; }
        }
        public int X1
        {
            get { return _x1; }
            set { _x1 = value; }
        }
        public int Y1
        {
            get { return _y1; }
            set { _y1 = value; }
        }
        public int X2
        {
            get { return _x2; }
            set { _x1 = value; }
        }
        public int Y2
        {
            get { return _y2; }
            set { _y2 = value; }
        }
        public int X3
        {
            get { return _x3; }
            set { _x3 = value; }
        }
        public int Y3
        {
            get { return _y3; }
            set { _y3 = value; }
        }
        
        public Module(ModuleType type, int id, int x, int y, int w, int h)
        {
            Type = type;
            Id = id;
            X = x;
            Y = y;
            Width = w;
            Height = h;
        }
    }
}
