using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;

namespace SpriteLightRock.Sprites
{
    enum ViewState{
        Module,
        FModule,
        Frame,
        AFrame,
        VFrame
    }
    class Sprite
    {
        private ObservableCollection<Module> _listModules;
        private List<FModule>   _listFModules;
        private List<Frame>     _listFrames;
        private List<AFrame>    _listAFrames;
        private string          _imagePath;
        private ViewState       _viewState;
        //@Reference: http://www.switchonthecode.com/tutorials/wpf-tutorial-using-the-listview-part-1
        //&http://msdn.microsoft.com/en-us/library/ms752347%28v=VS.85%29.aspx
        public ObservableCollection<Module> Modules
        {
            get
            {
                return _listModules;
            }
            private set
            {
                _listModules = value;
            }
        }
        public ViewState View
        {
            get
            {
                return _viewState;
            }
            set
            {
                _viewState = value;
            }
        }
        public string ImagePath
        {
            get { return _imagePath; }
        }
        public void SetImage(string path)
        {
            _imagePath = path;
        }
        public Sprite()
        {
            Modules = new ObservableCollection<Module>();
            View        = ViewState.Module;
        }
    }
}
