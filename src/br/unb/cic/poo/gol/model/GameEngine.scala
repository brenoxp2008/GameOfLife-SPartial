package br.unb.cic.poo.gol.model

import scala.collection.mutable.ListBuffer
import br.unb.cic.poo.gol.view.Cell
import br.unb.cic.poo.gol.Main
import br.unb.cic.poo.gol.view.commandline.Statistics
import br.unb.cic.poo.gol.controller.GameController
import br.unb.cic.poo.gol.model.rules.Rule

object GameEngine {
  
  val height = Main.height
  val width = Main.width
  
  val cells = Array.ofDim[Cell](height, width)
  
  //Rule deve ser definido como def pois sua avaliação deve ser feita sempre que
  //for chamada. Isso porque poderá ser alterada durante a execução do programa
  def rule = Main.rule
  
  for(i <- (0 until height)) {
    for(j <- (0 until width)) {
      cells(i)(j) = new Cell
    }
  }


  /**
	 * Calcula uma nova geracao do ambiente. Essa implementacao utiliza o
	 * algoritmo do Conway, ou seja:
	 * 
	 * a) uma celula morta com exatamente tres celulas vizinhas vivas se torna
	 * uma celula viva.
	 * 
	 * b) uma celula viva com duas ou tres celulas vizinhas vivas permanece
	 * viva.
	 * 
	 * c) em todos os outros casos a celula morre ou continua morta.
	 */
  
  def nextGeneration {
    
    
    val mustRevive = new ListBuffer[Cell]
    val mustKill = new ListBuffer[Cell]

    
    for(i <- (0 until height)) {
      for(j <- (0 until width)) {
        
        if(rule.shouldRevive(cells(i)(j).isAlive, numberOfNeighborhoodAliveCells(i, j))){
          mustRevive += cells(i)(j)
        }
        else if(!rule.shouldKeepAlive(cells(i)(j).isAlive, numberOfNeighborhoodAliveCells(i, j))){
          mustKill += cells(i)(j)
        }
      }
    }
    
    
    for(cell <- mustRevive) {
      cell.revive
      Statistics.recordRevive
    }
    
    for(cell <- mustKill) {
      cell.kill
      Statistics.recordKill
    }
    
    
  }

  /*
	 * Verifica se uma posicao (a, b) referencia uma celula valida no tabuleiro.
	 */
  private def validPosition(i: Int, j: Int) = 
    i >= 0 && i < height && j >= 0 && j < width;
  
  
  /**
	 * Torna a celula de posicao (i, j) viva
	 * 
	 * @param i posicao vertical da celula
	 * @param j posicao horizontal da celula
	 * 
	 * @throws InvalidParameterException caso a posicao (i, j) nao seja valida.
	 */
  @throws(classOf[IllegalArgumentException])
  def makeCellAlive(i: Int, j: Int) = {
    if(validPosition(i, j)){
      cells(i)(j).revive
      Statistics.recordRevive
    } else {
      throw new IllegalArgumentException
    }
  }
  
  /**
	 * Verifica se uma celula na posicao (i, j) estah viva.
	 * 
	 * @param i Posicao vertical da celula
	 * @param j Posicao horizontal da celula
	 * @return Verdadeiro caso a celula de posicao (i,j) esteja viva.
	 * 
	 * @throws InvalidParameterException caso a posicao (i,j) nao seja valida. 
	 */
  @throws(classOf[IllegalArgumentException])
  def isCellAlive(i: Int, j: Int): Boolean = {
    if(validPosition(i, j)) {
      cells(i)(j).isAlive
    } else {
      throw new IllegalArgumentException
    }
  }
  
  
  /**
	 * Retorna o numero de celulas vivas no ambiente. 
	 * Esse metodo eh particularmente util para o calculo de 
	 * estatisticas e para melhorar a testabilidade.
	 * 
	 * @return  numero de celulas vivas.
	 */
  def numberOfAliveCells {
    var aliveCells = 0
    for(i <- (0 until height)) {
      for(j <- (0 until width)) {
        if(isCellAlive(i, j)) aliveCells += 1
      }
    }
  }
  
  
//  /* verifica se uma celula deve ser mantida viva */
//  private def shouldKeepAlive(i: Int, j: Int): Boolean = {
//    (cells(i)(j).isAlive) &&
//      (numberOfNeighborhoodAliveCells(i, j) == 2 || numberOfNeighborhoodAliveCells(i, j) == 3)
//  }
//  
//  /* verifica se uma celula deve (re)nascer */
//  private def shouldRevive(i: Int, j: Int): Boolean = {
//    (!cells(i)(j).isAlive) && 
//      (numberOfNeighborhoodAliveCells(i, j) == 3)
//  }

  
  /*
	 * Computa o numero de celulas vizinhas vivas, dada uma posicao no ambiente
	 * de referencia identificada pelos argumentos (i,j).
	 */
  private def numberOfNeighborhoodAliveCells(i: Int, j: Int): Int = {
    var alive = 0
    for(a <- (i - 1 to i + 1)) {
      for(b <- (j - 1 to j + 1)) {
        val a1 = convertIToInfiniteWorld(a)
				val b1 = convertJToInfiniteWorld(b)
				
        if (validPosition(a1, b1)  && (!(a1==i && b1 == j)) && cells(a1)(b1).isAlive) {
					alive += 1
				}
      }
    }
    alive
  }
  

  
   /* 
  	 * Verifica se i eh uma posicao menor do que a origem do eixo x, 
  	 * caso seja, i passa a ser o maior valor aceito de x.
  	 * Se i for uma posicao maior do que o maior valor aceito para o eixo x,
  	 * entao o valor de i passa a ser o menor valor aceito de x.
	 */
  def convertIToInfiniteWorld(i: Int): Int = {
    if(i == -1) width - 1 
    else if(i == width) 0
    i
  }
  
  /* O memso tratamento para j */
  def convertJToInfiniteWorld(j: Int): Int = {
    if(j == -1) height - 1 
    else if(j == height) 0
    j
  }
  
  def randomCellsAlive() {    
    for(i <- (0 until height)) {
      for(j <- (0 until width)) {
        
        val randomValue = scala.util.Random.nextInt(100)
        
        if(randomValue < 50) {
          cells(i)(j).revive
          Statistics.recordRevive
        } else {
          cells(i)(j).kill
          Statistics.recordRevive
        }
        
      }
    }
    
    GameController.updateView
  }
  
  
  def halt() {
    println("\n \n")
    Statistics.display
    System.exit(0)
  }
  
  
  

}