using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace SpriteLightRock
{
    using Sprites;
    using SystemEx;
    /// <summary>
    /// Interaction logic for SprEditorForm.xaml
    /// </summary>
    public partial class SprFrameForm : UserControl
    {
    	CanvasEx canvas;
        public SprFrameForm()
        {
            InitializeComponent();
            Init();
        }
        public void Init()
        {
        	canvas = new CanvasEx();
            Sprite sprite = new Sprite();
            sprite.ImageFile = @"E:\vietean\LightRock\SpriteLightRock\Images\test.png";
            sprite.LoadImage();
            sprite.Modules.Add(new Module(1, 34, 59, 106, 76));
            sprite.Modules.Add(new Module(2, 16, 142, 146, 19));
            canvas.SetSprite(sprite);            
        }
    }    
}
