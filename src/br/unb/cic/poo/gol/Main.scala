package br.unb.cic.poo.gol

import br.unb.cic.poo.gol.controller.GameController
import br.unb.cic.poo.gol.view.GameView
import br.unb.cic.poo.gol.view.commandline.CommandLineView
import br.unb.cic.poo.gol.view.gui.GUIView
import br.unb.cic.poo.gol.model.ManufactureOfRules

object Main {
  
  val height = 10
  val width = 10
  
  val rule = ManufactureOfRules.getRule(1)
  
  var chooseViewType = 0
  var view: GameView = null;
  
  if(chooseViewType == 0) view = CommandLineView
  else view = GUIView
  
  def main(args: Array[String]){ 
    GameController.start
  }
}