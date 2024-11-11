import { useEffect, useState } from "react";
import { Form, LoaderFunction, useLoaderData } from "react-router-dom";
import { fetchProject } from "../../persistence/fetcher/fetcher";
import { ProjectResponse } from "../../persistence/model/responses";
import { Annotation, Label } from "../../persistence/model/responses";
import "../../styles/galery.css";
export const loader: LoaderFunction = async ({ params }) => {
    const project = await fetchProject(Number(params.projectId));
    return project;
}

export default function Project() {
    const [pageNum, setPageNum] = useState<number>(0);
    const [frameCount, setFrameCount] = useState<number>(0);
    const [startIndex, setStartIndex] = useState<number>(-1);
    const [endIndex, setEndIndex] = useState<number>(-1);
    const [selectedFrames, setSelectedFrames] = useState<number[]>([]);
    const project = useLoaderData() as ProjectResponse;
    const imagesPerPage = 100;
    const [labels, setLabels] = useState<Label[]>([]);
    const [currentLabel, setCurrentLabel] = useState<Label>();


    useEffect(() => {
        fetch('/api/projects/' + project.id + '/frame/count')
            .then((response) => response.json())
            .then((data) => setFrameCount(data.count))
            .catch((error) => console.error('Error fetching frame count:', error));


        fetch(`/api/projects/${project.id}/annotations`)
            .then((response) => {
                return response.json();
            })
            .then((data: Annotation[]) => {
                const annotatedFrames = data.map(annotation => annotation.frameId);
                setSelectedFrames(annotatedFrames);
            })
            .catch((error) => console.error('Error fetching annotations:', error));

    }, [project.id]);

    useEffect(() => {
        fetch(`/api/labels`)
            .then((response) => response.json())
            .then((data) => setLabels(data))
            .catch((error) => console.error('Error fetching labels:', error));
    }, []);

    const nextPage = () => {
        if ((pageNum + 1) * imagesPerPage < frameCount) {
            setPageNum(pageNum + 1);
        }
    };

    const prevPage = () => {
        if (pageNum > 0) {
            setPageNum(pageNum - 1);
        }
    };

    const handleMouseDown = (frameId: number) => {
        setStartIndex(frameId);

        console.log("Down at ", frameId);
        
    
        setSelectedFrames([...selectedFrames, frameId]);
    };

    const handleMouseUp = (frameId: number) => {
        console.log("Up at ", frameId);
        setEndIndex(frameId);

        setStartIndex(-1);

        fetch(`/api/projects/${project.id}/annotate/${startIndex}/${frameId}`, {
            method: 'POST',
        });
      };

    
    const handleMouseOver = (frameId: number) => {
        const framesToAdd: number[] = [];
        for (let index = startIndex; index <= frameId; index++) {
            if (startIndex != -1 && !selectedFrames.includes(index)) {
                framesToAdd.push(index);
            }
        }
        setSelectedFrames(selectedFrames => [...selectedFrames, ...framesToAdd]);                
    };


    const handleImageClick = (frameId: number) => {
        if (selectedFrames.includes(frameId)) {
            setSelectedFrames(selectedFrames.filter(id => id !== frameId));
        }
        else {
            setSelectedFrames([...selectedFrames, frameId]);
        }

        fetch(`/api/projects/${project.id}/annotate/${frameId}`, {
            method: 'POST',
        });
    }

    const preventDragHandler = (e: Event) => {
        e.preventDefault();
    }

    const startPosition = pageNum * imagesPerPage + 1;
    const endPosition = Math.min(startPosition + imagesPerPage - 1, frameCount);

    const imagePositions = Array.from(
        { length: endPosition - startPosition + 1 },
        (_, i) => startPosition + i
    );

    return (
        <div>
            <h1>{project ? project.projectName : 'No project found'}</h1>

            <select name="label" id="label-select">
                {labels.map((label) => (
                    <option value={label.label} key={"label_" + label.label}
                            label-id={label.id} label-name={label.label}> 
                        {label.label}
                    </option>
                ))}
            </select>

            <div className="image-grid">
                {imagePositions.map(position => (
                    <img
                        key={position}
                        src={`/api/projects/${project.id}/frame/${position}`}
                        alt={`Frame ${position}`}
                        className={`image ${selectedFrames.includes(position) ? 'selected' : ''}`} // Add selected class
                       // onClick={() => handleImageClick(position)}
                        onMouseDown={() => handleMouseDown(position)}
                        onMouseUp={() => handleMouseUp(position)}
                        onMouseOver={() => handleMouseOver(position)}
                        onDragStart={() => preventDragHandler}
                        draggable="false"
                    />
                ))}
            </div>
            <div className="pagination-buttons">
                <button onClick={prevPage} disabled={pageNum === 0}>Back</button>
                <button onClick={nextPage} disabled={(pageNum + 1) * imagesPerPage >= frameCount}>Next</button>
            </div>
        </div>
    );
}