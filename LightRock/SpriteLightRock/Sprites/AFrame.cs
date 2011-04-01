using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SpriteLightRock.Sprites
{
    class AFrame
    {
        private int _id;
        private List<Frame> _listFrames;
        private int _x;
        private int _y;
        private int _scaleX;
        private int _scaleY;
        private int _angle;
        public int Id
        {
            get { return _id; }
            set { _id = value; }
        }
        public List<Frame> Frames
        {
            get { return _listFrames; }
            set { _listFrames = value; }
        }
        public int X
        {
            get { return _x; }
            set { _x = value; }
        }
        public int Y
        {
            get { return _y; }
            set { _y = value; }
        }
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
        public int Angle
        {
            get { return _angle; }
            set { _angle = value; }
        }
    }
}
