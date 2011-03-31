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
    using Sprite;    
	/// <summary>
	/// Interaction logic for Window1.xaml
	/// </summary>
	public partial class SprEditorForm : Window
	{
		public SprEditorForm()
		{
			InitializeComponent();
            ArrayList arrListModules = new ArrayList();
            for (int i = 0; i < 10; i++)
            {
                arrListModules.Add(new Module(1, 2, 3, 4));
            }
            this.lvModules.ItemsSource = arrListModules;
            cnvWorkspace.Background = Brushes.Gray;
		}

        private void cnvWorkspace_MouseMove(object sender, MouseEventArgs e)
        {
            Canvas canvas = sender as Canvas;            
            Ellipse el = new Ellipse();
            el.Width = 2;
            el.Height = 2;
            el.Margin = new Thickness(e.GetPosition(canvas).X, e.GetPosition(canvas).Y, 0, 0);
            el.Fill = Brushes.LimeGreen;
            canvas.Children.Add(el);            
        }
	}
}