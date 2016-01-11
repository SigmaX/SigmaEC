# An example of how to view images of objective functions with the data that is output by TestSuiteViewerExperiment
#
# Eric O. Scott

plot_objective_2d <- function(fileName, title_text)
{
  data <- read.csv(fileName, header=F, stringsAsFactors=FALSE);
  x_grid_points <- data$V1[2:length(data$V1)]
  y_grid_points <- as.numeric(data[1,][2:length(data[1,])])
  mat <- data.matrix(data[2:length(data), 1:length(data)])[,-1]
  max_value <- mat[which(mat==max(mat), arr.ind=T)][1];
  min_value <- mat[which(mat==min(mat), arr.ind=T)][1];
  par(mai=c(.1,.1,.3,.1))
  persp(x_grid_points, y_grid_points, mat, theta=55, phi=25, xlab="x1", ylab="x2", zlab="Fitness", zlim=c(min_value, 1.5*max_value), col=c("gray", "pink", "orange"), ticktype="detailed")
  title(title_text)
}

library(ggplot2)
plot_objective_1d <- function(fileName, title_text) {
  data <- read.csv(fileName, header=F, stringsAsFactors=FALSE)
  ggplot(data, aes(x=V1, y=V2)) +
    geom_line() +
    ggtitle(title_text)
}

plot_objective <- function(fileName, title_text) {
  data <- read.csv(fileName, header=F, stringsAsFactors=FALSE);
  if (ncol(data) == 2)
    plot_objective_1d(fileName, title_text)
  else
    plot_objective_2d(fileName, title_text)
}

setwd("results/")
plot_objective("Function_0.csv", "Obj")
plot_objective("Function_1.csv", "Obj")
plot_objective("Function_2.csv", "Obj")
plot_objective("Function_3.csv", "Obj")
plot_objective("Function_4.csv", "Obj")
plot_objective("Function_5.csv", "Obj")
plot_objective("Function_6.csv", "Obj")
plot_objective("Function_7.csv", "Obj")
plot_objective("Function_8.csv", "Obj")
plot_objective("Function_9.csv", "Obj")
plot_objective("Function_10.csv", "Obj")
plot_objective("Function_11.csv", "Obj")
plot_objective("Function_12.csv", "Obj")
plot_objective("Function_13.csv", "Obj")
