package resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import dao.MovieDao;

@Path("/bygenre")
public class MovieResource {
	
	public MovieResource() {

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getMovieCountByGenre() {
		return MovieDao.instance.getMovieCountByGenre();
	}

}
