﻿using System;
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
    /// <summary>
    /// Interaction logic for SprEditorForm.xaml
    /// </summary>
    public partial class SprEditorForm : UserControl
    {
        Sprite sprite = new Sprite();
        public SprEditorForm()
        {
            InitializeComponent();            
            sprite.SetImage(@"d:\Images\embe.jpg");
            for(int i = 0; i < 10; i++)
            {
                sprite.Modules.Add(new Module(i+1,1,2,3,4));
            }
            lvViewModules.ItemsSource = sprite.Modules;            
            canvasEx1.SetSprite(sprite);
            MouseMove += new MouseEventHandler(SprEditorForm_MouseMove);
        }

        void SprEditorForm_MouseMove(object sender, MouseEventArgs e)
        {
            
            //lvViewModules.UpdateLayout();
            //lvViewModules.InvalidateVisual();            
            //lvViewModules.InvalidateProperty(ListView.ItemsSourceProperty);
            lvViewModules.InvalidateProperty(ListView.ItemsSourceProperty);
            //this.InvalidateVisual();
            //lvViewModules.ItemsSource = sprite.Modules;            
        }
    }
}
