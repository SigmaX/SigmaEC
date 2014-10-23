plot_objective <- function(dataAsText, title_text)
{
  data <- read.csv(text=dataAsText, header=F, stringsAsFactors=FALSE);
  x_grid_points <- data$V1[2:length(data$V1)]
  y_grid_points <- as.numeric(data[1,][2:length(data[1,])])
  mat <- data.matrix(data[2:length(data), 1:length(data)])[,-1]
  max_value <- mat[which(mat==max(mat), arr.ind=T)][1];
  min_value <- mat[which(mat==min(mat), arr.ind=T)][1];
  par(mai=c(.1,.1,.3,.1))
  persp(x_grid_points, y_grid_points, mat, theta=55, phi=25, xlab="x1", ylab="x2", zlab="Fitness", zlim=c(min_value, 1.5*max_value))
  title(title_text)
}