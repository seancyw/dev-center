using System;
using System.Collections.Generic;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Collections;
namespace SpriteLightRock.SystemEx
{
    using Sprites;
    class CanvasEx : Canvas
    {
        private Sprite          _currentSprite;        
        private Point           _currentPoint;
        
        public Sprite CurrentSprite
        {
            get
            {
                return _currentSprite;
            }
            set
            {
                _currentSprite = value;
            }
        }        
        
        public CanvasEx()
        	:base()
        {
        }
        public bool IsDrag
        {
            get;
            set;
        }
        public Point PointFressed
        {
            get;
            set;
        }
        public Point PointCurrent
        {
            get { return _currentPoint; }
            set{
                if (_currentPoint != value)
                {
                    _currentPoint = value;
                    this.InvalidateVisual();
                }
            }
        }
        protected override void OnRender(DrawingContext dc)
        {
            base.OnRender(dc);
            if (CurrentSprite == null)
                return;
            Transform t = Transform.Identity;    
            dc.PushTransform(new RotateTransform(45,Width/2,Height/2));
            dc.PushTransform(new ScaleTransform(0.25f,0.25f));
            dc.PushTransform(new TranslateTransform(200,300));
            dc.DrawImage(CurrentSprite.Bitmap, new Rect(30,20,CurrentSprite.Bitmap.Width,CurrentSprite.Bitmap.Height));
        }
        protected override void OnMouseDown(MouseButtonEventArgs e)
        {
            base.OnMouseDown(e);
            if (IsDrag == false)
            {
                IsDrag = true;
                PointFressed = e.GetPosition(this);                
            }
        }
        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            PointCurrent = e.GetPosition(this);
        }
        protected override void OnMouseUp(MouseButtonEventArgs e)
        {
            base.OnMouseUp(e);
            if (IsDrag == true)
            {
                Rect rect = new Rect(PointFressed, PointCurrent);
                CurrentSprite.Modules.Add(new Module(CurrentSprite.GetAutoGenerateModuleId(), (int)rect.Left, (int)rect.Top, (int)rect.Width, (int)rect.Height));
                IsDrag = false;
            }
        }
    }
}
