using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SpriteLightRock.Sprites
{
    class Sprite
    {
        private List<Module> _listModules;
        private List<FModule> _listFModules;
        private List<Frame> _listFrames;
        private List<AFrame> _listAFrames;
        public List<Module> Modules
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
        public Sprite()
        {
            Modules = new List<Module>();
        }
    }
}
