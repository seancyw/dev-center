/*
 * Created by SharpDevelop.
 * User: phu.tranphong
 * Date: 3/30/2011
 * Time: 1:24 PM
 * 
 * To change this template use Tools | Options | Coding | Edit Standard Headers.
 */
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

namespace SpriteLightRock
{
    using Sprites;
    using System.Windows.Threading;    
	/// <summary>
	/// Interaction logic for Window1.xaml
	/// </summary>
	public partial class SprEditorForm : Window
	{
        Point point1
        {
            get;
            set;
        }
        Point point2
        {
            get;
            set;
        }
        Boolean IsDrag
        {
            get;
            set;
        }
		public SprEditorForm()
		{
			InitializeComponent();
            Sprite sprite = new Sprite();
            for (int i = 0; i < 10; i++)
            {
                sprite.Modules.Add(new Module(i+1,1, 2, 3, 4));
            }
            this.lvModules.ItemsSource = sprite.Modules;
            ImageSourceConverter imgConv = new ImageSourceConverter();
            //string path = "pack://application:,,/Images/embe.jpg";
            string path = @"f:\__Devs\dev_center\LightRock\SpriteLightRock\Images\embe.jpg";
            ImageSource imageSource = (ImageSource) imgConv.ConvertFromString(path);
            this.cnvWorkspace.SpriteImage = imageSource;
            this.cnvWorkspace.MouseMove += new MouseEventHandler(cnvWorkspace_MouseMove);
		}

        void cnvWorkspace_MouseMove(object sender, MouseEventArgs e)
        {
            cnvWorkspace.InvalidateVisual();
            //Khong xai dc
           // cnvWorkspace.Refresh();
        }        
	}
    public static class ExtensionMethods
    {

        private static Action EmptyDelegate = delegate() { };


        public static void Refresh(this UIElement uiElement)
        {
            uiElement.Dispatcher.Invoke(DispatcherPriority.Render, EmptyDelegate);
        }
    }
}