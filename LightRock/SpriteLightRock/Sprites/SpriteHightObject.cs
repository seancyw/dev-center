using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SpriteLightRock.Sprites
{
    class SpriteHightObject : SpriteLowObject
    {
        private int _anchorX;
        private int _anchorY;
        private int _scaleX;
        private int _scaleY;
        private int _angle;
        public int ScaleX
        {
            get { return _scaleX; }
            set { _scaleX = value; }
        }
        public int ScaleY
        {
            get { return _scaleY; }
            set { _scaleY = value; }
        }
        public int AnchorX
        {
            get { return _anchorX; }
            set { _anchorX = value; }
        }
        public int AnchorY
        {
            get { return _anchorY; }
            set { _anchorY = value; }
        }
        public int Angle
        {
            get { return _angle; }
            set { _angle = value; }
        }        
    }
}
