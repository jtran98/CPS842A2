package invert;

public class Document {
	String id;
	String title;
	String articleAbstract;
	String publicationDate;
	String authorList;
	String content;
	public Document(String id, String title, String articleAbstract, String publicationDate, String authorList) {
		this.id = id;
		this.title = title;
		this.articleAbstract = articleAbstract;
		this.publicationDate = publicationDate;
		this.authorList = authorList;
		content = title + "\n" + articleAbstract;
	}
	public Document() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbstract() {
		return articleAbstract;
	}
	public void setAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
	}
	public String getDate() {
		return publicationDate;
	}
	public void setDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	public String getAuthors() {
		return authorList;
	}
	public void setAuthors(String authorList) {
		this.authorList = authorList;
	}
	public String getcontent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
